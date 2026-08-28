import type { SVGProps } from "react";
type Props = SVGProps<SVGSVGElement>;
const Base = ({ children, ...props }: Props) => (
  <svg
    viewBox="0 0 24 24"
    fill="none"
    stroke="currentColor"
    strokeWidth="1.8"
    strokeLinecap="round"
    strokeLinejoin="round"
    aria-hidden="true"
    {...props}
  >
    {children}
  </svg>
);
export const SearchSvg = (p: Props) => (
  <Base {...p}>
    <circle cx="11" cy="11" r="7" />
    <path d="m20 20-4-4" />
  </Base>
);
export const MenuSvg = (p: Props) => (
  <Base {...p}>
    <path d="M4 7h16M4 12h16M4 17h16" />
  </Base>
);
export const CloseSvg = (p: Props) => (
  <Base {...p}>
    <path d="m6 6 12 12M18 6 6 18" />
  </Base>
);
export const ArrowSvg = (p: Props) => (
  <Base {...p}>
    <path d="M5 12h14m-5-5 5 5-5 5" />
  </Base>
);
export const CartSvg = (p: Props) => (
  <Base {...p}>
    <path d="M3 4h2l2.3 10h9.8l2-7H6" />
    <circle cx="9" cy="19" r="1" />
    <circle cx="17" cy="19" r="1" />
  </Base>
);
