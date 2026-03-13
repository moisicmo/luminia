import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"),
  route("register-business", "routes/register-business.tsx"),
  route("admin/*?", "routes/admin/admin.route.tsx"),
] satisfies RouteConfig;
