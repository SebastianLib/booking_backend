import { Field, Int, ObjectType } from "@nestjs/graphql";
import { UserRole } from "src/enums/user-role.enum";

@ObjectType()
export class AuthPayload {
  @Field(() => Int)
  id: number;

  @Field()
  username: string;

  @Field(() => UserRole)
  role: UserRole;

  @Field()
  accessToken: string;

  @Field()
  isVerified: boolean;

  @Field({ nullable: true })
  phoneNumber?: string;
}