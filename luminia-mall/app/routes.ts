import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("register-business", "routes/register-business.tsx"),
] satisfies RouteConfig;
