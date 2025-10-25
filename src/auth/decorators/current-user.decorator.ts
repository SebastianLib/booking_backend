import { createParamDecorator, ExecutionContext } from '@nestjs/common';
import { GqlExecutionContext } from '@nestjs/graphql';
import { Request } from 'express';
import { AuthJwtPayload } from '@/auth/types/auth-jwt-payload';
import { UserRole } from '@/enums/user-role.enum';

interface GqlContext {
  req: Request & { user?: AuthJwtPayload & { role?: UserRole[] } };
}

export const CurrentUser = createParamDecorator(
  (data: unknown, context: ExecutionContext) => {
    const gqlCtx = GqlExecutionContext.create(context);
    const ctx = gqlCtx.getContext<GqlContext>();
    return ctx.req.user;
  },
);
