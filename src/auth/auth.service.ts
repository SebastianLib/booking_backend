import {
  BadRequestException,
  Injectable,
  UnauthorizedException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { hash, verify } from 'argon2';
import { UserRole } from 'src/enums/user-role.enum';
import { CreateUserInput } from 'src/users/dto/create-user.input';
import { User } from 'src/users/entities/user.entity';
import { Repository } from 'typeorm';
import { SignInInput } from './dto/signIn-input';
import { AuthJwtPayload } from './types/auth-jwt-payload';
import { JwtService } from '@nestjs/jwt';
import { AuthPayload } from './entities/auth-payload';
import { JwtUser } from './types/jwt-user';

@Injectable()
export class AuthService {
  constructor(
    @InjectRepository(User) private userRepository: Repository<User>,
    private readonly jwtService: JwtService,
  ) {}

  async registerUser(input: CreateUserInput) {
    const existingUser = await this.userRepository.findOne({
      where: { email: input.email },
    });

    if (existingUser) {
      throw new BadRequestException('User already exists');
    }

    const hashedPassword = await hash(input.password);

    const newUser = this.userRepository.create({
      ...input,
      password: hashedPassword,
      role: UserRole.OWNER,
    });

    const savedUser = await this.userRepository.save(newUser);
    const { accessToken } = await this.generateToken(savedUser.id);
    return {
      id: savedUser.id,
      username: savedUser.username,
      role: savedUser.role,
      isVerified: savedUser.isVerified,
      phoneNumber: savedUser.phoneNumber,
      accessToken,
    };
  }

  async validateUser({ email, password }: SignInInput) {
    const user = await this.userRepository.findOneOrFail({ where: { email } });

    const passwordMatched = await verify(user.password, password);

    if (!passwordMatched)
      throw new UnauthorizedException('Invalid credentials');

    return user;
  }

  async generateToken(userId: number) {
    const payload: AuthJwtPayload = {
      sub: {
        userId,
      },
    };
    const accessToken = await this.jwtService.signAsync(payload);
    return { accessToken };
  }

  async login(user: User): Promise<AuthPayload> {
    const { accessToken } = await this.generateToken(user.id);
    return {
      id: user.id,
      username: user.username,
      role: user.role,
      isVerified: user.isVerified,
      phoneNumber: user.phoneNumber,
      accessToken,
    };
  }

  async validateJwtUser(userId: number) {
    const user = await this.userRepository.findOneOrFail({
      where: { id: userId },
    });
    const jwtUser: JwtUser = {
      userId: user.id,
      role: user.role,
    };
    return jwtUser;
  }

  async validateGoogleUser({ email, username }: CreateUserInput) {
    const user = await this.userRepository.findOne({ where: { email } });

    if (user) {
      const { password, ...authUser } = user;
      return authUser;
    }

    const newUser = this.userRepository.create({
      email,
      username,
      password: '',
    });

    await this.userRepository.save(newUser);
    return newUser;
  }
}
