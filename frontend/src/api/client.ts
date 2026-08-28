import type { ApiErrorBody } from "../types/api";
const BASE_URL = (
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080"
).replace(/\/$/, "");
const TOKEN_KEY = "tsinjo_food_token";
export const session = {
  getToken: () => localStorage.getItem(TOKEN_KEY),
  setToken: (token: string) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
};
export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public fieldErrors: Record<string, string> = {},
    public path?: string,
  ) {
    super(message);
    this.name = "ApiError";
  }
}
export function parseApiError(body: unknown, status: number): ApiError {
  const data =
    body && typeof body === "object" ? (body as Partial<ApiErrorBody>) : {};
  return new ApiError(
    status,
    typeof data.message === "string"
      ? data.message
      : status >= 500
        ? "The service is temporarily unavailable."
        : "The request could not be completed.",
    data.fieldErrors ?? {},
    data.path,
  );
}
type RequestOptions = Omit<RequestInit, "body"> & {
  body?: unknown;
  auth?: boolean;
};
export async function api<T>(
  path: string,
  options: RequestOptions = {},
): Promise<T> {
  const token = session.getToken();
  const headers = new Headers(options.headers);
  headers.set("Accept", "application/json");
  if (options.body !== undefined)
    headers.set("Content-Type", "application/json");
  if (token && options.auth !== false)
    headers.set("Authorization", `Bearer ${token}`);
  let response: Response;
  try {
    response = await fetch(`${BASE_URL}${path}`, {
      ...options,
      headers,
      body:
        options.body === undefined ? undefined : JSON.stringify(options.body),
    });
  } catch {
    throw new ApiError(
      0,
      "Unable to reach the server. Check your connection and try again.",
    );
  }
  const text = await response.text();
  let data: unknown = null;
  if (text) {
    try {
      data = JSON.parse(text);
    } catch {
      data = text;
    }
  }
  if (!response.ok) {
    const error = parseApiError(data, response.status);
    if (response.status === 401 && token) {
      session.clear();
      window.dispatchEvent(new Event("auth:unauthorized"));
    }
    throw error;
  }
  return data as T;
}
