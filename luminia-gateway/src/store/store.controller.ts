import {
  Body,
  Controller,
  Delete,
  Get,
  Param,
  Patch,
  Post,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiBearerAuth, ApiHeader, ApiOperation, ApiTags } from '@nestjs/swagger';
import { User, CurrentUser } from '../common/decorators/current-user.decorator';
import { BusinessId } from '../common/decorators/business-id.decorator';
import { BusinessGuard } from '../common/guards/business.guard';
import { MinRole } from '../common/decorators/min-role.decorator';
import { MemberRole } from '../common/decorators/min-role.decorator';
import { StoreService } from './store.service';

@ApiTags('Store')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@UseGuards(BusinessGuard)
@Controller('store')
export class StoreController {
  constructor(private readonly storeService: StoreService) {}

  // ─── Categories ─────────────────────────────────────────────────────────────

  @Post('categories')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear categoría' })
  createCategory(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.storeService.createCategory(businessId, body, user.sub);
  }

  @Get('categories')
  @ApiOperation({ summary: 'Listar categorías del negocio' })
  listCategories(@BusinessId() businessId: string) {
    return this.storeService.listCategories(businessId);
  }

  @Patch('categories/:id')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Actualizar categoría' })
  updateCategory(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.updateCategory(id, businessId, body, user.sub);
  }

  @Delete('categories/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar categoría' })
  removeCategory(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.removeCategory(id, businessId, user.sub);
  }

  // ─── Brands ─────────────────────────────────────────────────────────────────

  @Post('brands')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear marca' })
  createBrand(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.storeService.createBrand(businessId, body, user.sub);
  }

  @Get('brands')
  @ApiOperation({ summary: 'Listar marcas del negocio' })
  listBrands(@BusinessId() businessId: string) {
    return this.storeService.listBrands(businessId);
  }

  @Patch('brands/:id')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Actualizar marca' })
  updateBrand(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.updateBrand(id, businessId, body, user.sub);
  }

  @Delete('brands/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar marca' })
  removeBrand(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.removeBrand(id, businessId, user.sub);
  }

  // ─── Suppliers ───────────────────────────────────────────────────────────────

  @Post('suppliers')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear proveedor' })
  createSupplier(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.storeService.createSupplier(businessId, body, user.sub);
  }

  @Get('suppliers')
  @ApiOperation({ summary: 'Listar proveedores del negocio' })
  listSuppliers(@BusinessId() businessId: string) {
    return this.storeService.listSuppliers(businessId);
  }

  @Patch('suppliers/:id')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Actualizar proveedor' })
  updateSupplier(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.updateSupplier(id, businessId, body, user.sub);
  }

  @Delete('suppliers/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar proveedor' })
  removeSupplier(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.removeSupplier(id, businessId, user.sub);
  }

  // ─── Units ───────────────────────────────────────────────────────────────────

  @Post('units')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear unidad de medida' })
  createUnit(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.storeService.createUnit(businessId, body, user.sub);
  }

  @Get('units')
  @ApiOperation({ summary: 'Listar unidades de medida del negocio' })
  listUnits(@BusinessId() businessId: string) {
    return this.storeService.listUnits(businessId);
  }

  @Patch('units/:id')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Actualizar unidad de medida' })
  updateUnit(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.updateUnit(id, businessId, body, user.sub);
  }

  // ─── Products ────────────────────────────────────────────────────────────────

  @Post('products')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear producto' })
  createProduct(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.storeService.createProduct(businessId, body, user.sub);
  }

  @Get('products')
  @ApiOperation({ summary: 'Listar productos del negocio' })
  listProducts(
    @BusinessId() businessId: string,
    @Query('categoryId') categoryId?: string,
    @Query('brandId') brandId?: string,
    @Query('search') search?: string,
  ) {
    return this.storeService.listProducts(businessId, { categoryId, brandId, search });
  }

  @Get('products/:id')
  @ApiOperation({ summary: 'Obtener producto por ID' })
  findProduct(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.storeService.findProduct(id, businessId);
  }

  @Patch('products/:id')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Actualizar producto' })
  updateProduct(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.updateProduct(id, businessId, body, user.sub);
  }

  @Delete('products/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar producto' })
  removeProduct(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.removeProduct(id, businessId, user.sub);
  }

  // ─── Product Units ─────────────────────────────────────────────────────────

  @Post('products/:productId/units')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Agregar unidad al producto' })
  addProductUnit(
    @Param('productId') productId: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.addProductUnit(productId, businessId, body, user.sub);
  }

  @Patch('products/:productId/units/:unitId')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Actualizar unidad del producto' })
  updateProductUnit(
    @Param('productId') productId: string,
    @Param('unitId') productUnitId: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.updateProductUnit(productUnitId, productId, businessId, body, user.sub);
  }

  @Delete('products/:productId/units/:unitId')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Eliminar unidad del producto' })
  removeProductUnit(
    @Param('productId') productId: string,
    @Param('unitId') productUnitId: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.storeService.removeProductUnit(productUnitId, productId, businessId, user.sub);
  }
}
