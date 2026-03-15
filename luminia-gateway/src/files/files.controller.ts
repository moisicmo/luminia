import {
  Controller,
  Post,
  Get,
  Delete,
  Param,
  Query,
  UseGuards,
  UseInterceptors,
  UploadedFile,
  HttpException,
  HttpStatus,
} from '@nestjs/common';
import { FileInterceptor } from '@nestjs/platform-express';
import { diskStorage } from 'multer';
import { ApiBearerAuth, ApiConsumes, ApiOperation, ApiTags } from '@nestjs/swagger';
import { extname, join } from 'path';
import { mkdirSync } from 'fs';
import { randomUUID } from 'crypto';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessId } from '../common/decorators/business-id.decorator';
import { BusinessGuard } from '../common/guards/business.guard';
import { FilesService } from './files.service';

const UPLOADS_DIR = join(process.cwd(), 'uploads');
mkdirSync(UPLOADS_DIR, { recursive: true });
const MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB
const ALLOWED_MIME_TYPES = [
  'image/jpeg',
  'image/png',
  'image/webp',
  'image/gif',
  'image/svg+xml',
  'application/pdf',
];

@ApiTags('Files')
@ApiBearerAuth('Authorization')
@Controller('files')
export class FilesController {
  constructor(private readonly filesService: FilesService) {}

  @Post('upload')
  @UseGuards(BusinessGuard)
  @ApiConsumes('multipart/form-data')
  @ApiOperation({ summary: 'Subir archivo (imagen, PDF, etc.)' })
  @UseInterceptors(
    FileInterceptor('file', {
      storage: diskStorage({
        destination: UPLOADS_DIR,
        filename: (_req, file, cb) => {
          const uniqueName = `${randomUUID()}${extname(file.originalname)}`;
          cb(null, uniqueName);
        },
      }),
      limits: { fileSize: MAX_FILE_SIZE },
      fileFilter: (_req, file, cb) => {
        if (ALLOWED_MIME_TYPES.includes(file.mimetype)) {
          cb(null, true);
        } else {
          cb(
            new HttpException(
              `Tipo de archivo no permitido: ${file.mimetype}`,
              HttpStatus.BAD_REQUEST,
            ),
            false,
          );
        }
      },
    }),
  )
  async upload(
    @UploadedFile() file: Express.Multer.File,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
    @Query('category') category?: string,
  ) {
    if (!file) {
      throw new HttpException('No se envió ningún archivo', HttpStatus.BAD_REQUEST);
    }

    const isImage = file.mimetype.startsWith('image/');
    const fileCategory = category || (isImage ? 'IMAGE' : 'DOCUMENT');
    const url = `/api/files/assets/${file.filename}`;

    const record = await this.filesService.createFileRecord({
      businessId,
      category: fileCategory,
      mimeType: file.mimetype,
      originalName: file.originalname,
      storageKey: file.filename,
      url,
      sizeBytes: file.size,
      visibility: 'PUBLIC',
      createdBy: user.sub,
    });

    return record;
  }

  @Get()
  @UseGuards(BusinessGuard)
  @ApiOperation({ summary: 'Listar archivos del negocio' })
  listFiles(
    @BusinessId() businessId: string,
    @Query('category') category?: string,
  ) {
    return this.filesService.listFiles(businessId, category);
  }

  @Delete(':id')
  @UseGuards(BusinessGuard)
  @ApiOperation({ summary: 'Eliminar archivo' })
  deleteFile(@Param('id') id: string) {
    return this.filesService.deleteFile(id);
  }
}
