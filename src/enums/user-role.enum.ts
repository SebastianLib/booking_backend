import { registerEnumType } from '@nestjs/graphql';

export enum UserRole {
  OWNER = 'owner',
  WORKER = 'worker',
  CUSTOMER = 'customer',
  ADMIN = 'admin',
}

registerEnumType(UserRole, {
  name: 'UserRole',
});
