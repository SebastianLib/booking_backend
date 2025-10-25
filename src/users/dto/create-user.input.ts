import { InputType, Field } from '@nestjs/graphql';
import { IsEmail, IsString, MinLength } from 'class-validator';
import { UserRole } from 'src/enums/user-role.enum';

@InputType()
export class CreateUserInput {
  @IsString()
  @Field()
  username: string;

  @IsString()
  @IsEmail()
  @Field()
  email: string;

  // @IsEnum(UserRole)
  // @Field(() => UserRole)
  // role: UserRole;

  @IsString()
  @Field()
  @MinLength(3)
  password: string;
}
