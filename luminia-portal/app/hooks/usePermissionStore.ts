import type { TypeAction, TypeSubject } from '@/models';
export const usePermissionStore = () => ({
  hasPermission: (_action: TypeAction, _subject: TypeSubject) => false,
  requirePermission: (_action: TypeAction, _subject: TypeSubject) => {},
});
