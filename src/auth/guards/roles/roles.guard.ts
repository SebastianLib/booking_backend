import { CanActivate, ExecutionContext, Injectable } from '@nestjs/common';
import { Reflector } from '@nestjs/core';
import { GqlExecutionContext } from '@nestjs/graphql';
import { Observable } from 'rxjs';
import { ROLES_KEY } from 'src/auth/decorators/roles.decorator';
import { UserRole } from 'src/enums/user-role.enum';
import { Request } from 'express';
import { AuthJwtPayload } from '@/auth/types/auth-jwt-payload';

interface GqlContext {
  req: Request & { user?: AuthJwtPayload & { role?: UserRole[] } };
}

@Injectable()
export class RolesGuard implements CanActivate {
  constructor(private readonly reflector: Reflector) {}

  canActivate(
    context: ExecutionContext,
  ): boolean | Promise<boolean> | Observable<boolean> {
    const requiredRoles = this.reflector.getAllAndOverride<UserRole[]>(
      ROLES_KEY,
      [context.getHandler(), context.getClass()],
    );

    if (!requiredRoles) return true;

    const ctx = GqlExecutionContext.create(context);
    const gqlContext = ctx.getContext<GqlContext>();
    const user = gqlContext.req.user;

    console.log(user, requiredRoles);

    return !!user && requiredRoles.some((role) => user.role?.includes(role));
  }
}
