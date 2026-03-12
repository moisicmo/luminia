import { NestFactory } from '@nestjs/core';
import { AppModule } from './app.module';
import { MicroserviceOptions, Transport } from '@nestjs/microservices';
import { Logger, ValidationPipe } from '@nestjs/common';
import { envs, RMQServiceBusiness } from './config';

async function bootstrap() {

  const logger = new Logger('Main');

  const app = await NestFactory.create(AppModule);
  app.enableShutdownHooks();
  app.connectMicroservice<MicroserviceOptions>({
    transport: Transport.RMQ,
    options: {
      urls: envs.rmqServers,
      queue: RMQServiceBusiness.getQueueName(),
      queueOptions: { durable: true },
      socketOptions: {
        heartbeatIntervalInSeconds: 5,
        reconnectTimeInSeconds: 5,
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
  logger.log(`Businesses Microservice running on port ${envs.port}`);

}
bootstrap();