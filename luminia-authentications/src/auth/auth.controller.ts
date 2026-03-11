import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { AuthService } from './auth.service';
import { LoginDto } from './dto/login.dto';
import { RegisterAuthDto } from './dto/register.dto';

@Controller()
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @MessagePattern('auth.login')
  login(@Payload() dto: LoginDto) {
    return this.authService.login(dto);
  }

  @MessagePattern('auth.register')
  register(@Payload() dto: RegisterAuthDto) {
    return this.authService.register(dto);
  }

  @MessagePattern('auth.refresh')
  refresh(@Payload() dto: { refreshToken: string; userAgent?: string; ipAddress?: string }) {
    return this.authService.refreshToken(dto);
  }
}
