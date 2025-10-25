import { Controller, Get, Request, Res, UseGuards } from '@nestjs/common';
import { GoogleAuthGuard } from './guards/google-auth/google-auth.guard';
import { AuthService } from './auth.service';
import { GqlJwtGuard } from './guards/gql-jwt-guard/gql-jwt.guard';

@Controller('auth')
export class AuthController {
  constructor(private readonly authService: AuthService) {}
  @UseGuards(GoogleAuthGuard)
  @Get('google/login')
  googleLogin() {}

  @UseGuards(GoogleAuthGuard)
  @Get('google/callback')
  async googleCallback(@Request() req, @Res() res) {
    const userData = await this.authService.login(req.user);

    res.redirect(
      `http://localhost:3000/api/auth/google/callback?userId=${userData.id}&username=${userData.username}&role=${userData.role}&accessToken=${userData.accessToken}`,
    );
  }

  @UseGuards(GqlJwtGuard)
  @Get('verify-token')
  verify() {
    return 'ok';
  }
}
