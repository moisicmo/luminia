import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { Logger, ValidationPipe } from '@nestjs/common';
import { envs, RMQServiceCatalogs } from './config';

async function bootstrap() {

  const logger = new Logger('Main');

  const app = await NestFactory.create(AppModule);
  app.enableShutdownHooks();
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.RMQ,
    options: {
      urls: envs.rmqServers,
      queue: RMQServiceCatalogs.getQueueName(),
      queueOptions: {
        durable: true,
      },
    },
  });

  app.useGlobalPipes(
    new ValidationPipe({
      whitelist: true,
      forbidNonWhitelisted: true,
    }),
  );
  await app.startAllMicroservices();
  await app.listen(envs.port);
  logger.log(`Catalogs Microservice running on port ${envs.port}`);

}
bootstrap();