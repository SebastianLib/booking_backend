import { ObjectType, Field, Int } from '@nestjs/graphql';
import { UserRole } from 'src/enums/user-role.enum';
import { Column, Entity, PrimaryGeneratedColumn } from 'typeorm';

@Entity({ name: 'user' })
@ObjectType()
export class User {
  @PrimaryGeneratedColumn()
  @Field(() => Int)
  id: number;

  @Column()
  @Field()
  username: string;

  @Column({ unique: true })
  @Field()
  email: string;

  @Column({
    type: 'enum',
    enum: UserRole,
    default: UserRole.CUSTOMER,
  })
  @Field(() => UserRole)
  role: UserRole;

  @Column({ nullable: false })
  @Field()
  password: string;

  @Column({ nullable: true })
  @Field({ nullable: true })
  phoneNumber?: string;

  @Column({ default: false })
  @Field()
  isVerified: boolean;
}
