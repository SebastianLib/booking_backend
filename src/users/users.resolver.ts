import { Resolver, Query, Mutation, Args, Int } from '@nestjs/graphql';
import { UsersService } from './users.service';
import { User } from './entities/user.entity';
import { CreateUserInput } from './dto/create-user.input';
import { UpdateUserInput } from './dto/update-user.input';
import { GqlJwtGuard } from 'src/auth/guards/gql-jwt-guard/gql-jwt.guard';
import { UseGuards } from '@nestjs/common';
import type { JwtUser } from 'src/auth/types/jwt-user';
import { CurrentUser } from 'src/auth/decorators/current-user.decorator';
import { Roles } from 'src/auth/decorators/roles.decorator';
import { UserRole } from 'src/enums/user-role.enum';
import { RolesGuard } from 'src/auth/guards/roles/roles.guard';

@Resolver(() => User)
export class UsersResolver {
  constructor(private readonly usersService: UsersService) {}

  @Mutation(() => User)
  createUser(@Args('createUserInput') createUserInput: CreateUserInput) {
    return this.usersService.create(createUserInput);
  }

  @Query(() => [User], { name: 'users' })
  findAll() {
    return this.usersService.findAll();
  }

  @Query(() => User, { name: 'user' })
  findOne(@Args('id', { type: () => Int }) id: number) {
    return this.usersService.findOne(id);
  }

  @UseGuards(GqlJwtGuard)
  @Query(() => User, { name: 'currentUser' })
  getCurrentUser(@CurrentUser() user: JwtUser) {
    return this.usersService.findOne(user.userId);
  }

  @Roles(UserRole.OWNER)
  @UseGuards(GqlJwtGuard, RolesGuard)
  @Mutation(() => User)
  updateUser(
    @CurrentUser() user: JwtUser,
    @Args('updateUserInput') updateUserInput: UpdateUserInput,
  ) {
    return this.usersService.update(user.userId, updateUserInput);
  }

  @Mutation(() => User)
  removeUser(@Args('id', { type: () => Int }) id: number) {
    return this.usersService.remove(id);
  }
}
