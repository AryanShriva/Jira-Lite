export interface User {
  id: number;
  email: string;
  role: 'DEVELOPER' | 'TESTER' | 'MANAGER' | 'ADMIN';
}