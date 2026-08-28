export const formatPrice = (value: number) =>
  `${new Intl.NumberFormat("fr-MG", { maximumFractionDigits: 0 }).format(value)} Ar`;
export const formatDate = (value: string) =>
  new Intl.DateTimeFormat("en-MG", { dateStyle: "medium" }).format(
    new Date(value),
  );
export const formatDateTime = (value: string) =>
  new Intl.DateTimeFormat("en-MG", {
    dateStyle: "medium",
    timeStyle: "short",
  }).format(new Date(value));
export const parseId = (value: string | undefined) => {
  const id = Number(value);
  return Number.isFinite(id) && id > 0 ? id : 0;
};
