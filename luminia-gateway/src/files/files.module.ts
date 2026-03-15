import { Module } from '@nestjs/common';
import { ClientsModule, Transport } from '@nestjs/microservices';
import { ServeStaticModule } from '@nestjs/serve-static';
import { join } from 'path';
import { envs, RMQServiceFiles } from '@/config';
import { FilesController } from './files.controller';
import { FilesService } from './files.service';

@Module({
  imports: [
    ClientsModule.register([
      {
        name: RMQServiceFiles.getName(),
        transport: Transport.RMQ,
        options: {
          urls: envs.rmqServers,
          queue: RMQServiceFiles.getQueueName(),
          queueOptions: { durable: true },
        },
      },
    ]),
    ServeStaticModule.forRoot({
      rootPath: join(process.cwd(), 'uploads'),
      serveRoot: '/api/files/assets',
    }),
  ],
  controllers: [FilesController],
  providers: [FilesService],
})
export class FilesModule {}
