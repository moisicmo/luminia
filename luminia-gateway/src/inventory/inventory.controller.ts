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
import { InventoryService } from './inventory.service';

@ApiTags('Inventory')
@ApiBearerAuth('Authorization')
@ApiHeader({ name: 'X-Business-Id', required: true, description: 'ID del negocio activo' })
@UseGuards(BusinessGuard)
@Controller('inventory')
export class InventoryController {
  constructor(private readonly inventoryService: InventoryService) {}

  // ─── Categories ─────────────────────────────────────────────────────────────

  @Post('categories')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear categoría' })
  createCategory(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createCategory(businessId, body, user.sub);
  }

  @Get('categories')
  @ApiOperation({ summary: 'Listar categorías del negocio' })
  listCategories(@BusinessId() businessId: string) {
    return this.inventoryService.listCategories(businessId);
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
    return this.inventoryService.updateCategory(id, businessId, body, user.sub);
  }

  @Delete('categories/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar categoría' })
  removeCategory(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.inventoryService.removeCategory(id, businessId, user.sub);
  }

  // ─── Brands ─────────────────────────────────────────────────────────────────

  @Post('brands')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear marca' })
  createBrand(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createBrand(businessId, body, user.sub);
  }

  @Get('brands')
  @ApiOperation({ summary: 'Listar marcas del negocio' })
  listBrands(@BusinessId() businessId: string) {
    return this.inventoryService.listBrands(businessId);
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
    return this.inventoryService.updateBrand(id, businessId, body, user.sub);
  }

  @Delete('brands/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar marca' })
  removeBrand(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.inventoryService.removeBrand(id, businessId, user.sub);
  }

  // ─── Suppliers ───────────────────────────────────────────────────────────────

  @Post('suppliers')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear proveedor' })
  createSupplier(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createSupplier(businessId, body, user.sub);
  }

  @Get('suppliers')
  @ApiOperation({ summary: 'Listar proveedores del negocio' })
  listSuppliers(@BusinessId() businessId: string) {
    return this.inventoryService.listSuppliers(businessId);
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
    return this.inventoryService.updateSupplier(id, businessId, body, user.sub);
  }

  @Delete('suppliers/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar proveedor' })
  removeSupplier(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.inventoryService.removeSupplier(id, businessId, user.sub);
  }

  // ─── Units ───────────────────────────────────────────────────────────────────

  @Post('units')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear unidad de medida' })
  createUnit(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createUnit(businessId, body, user.sub);
  }

  @Get('units')
  @ApiOperation({ summary: 'Listar unidades de medida del negocio' })
  listUnits(@BusinessId() businessId: string) {
    return this.inventoryService.listUnits(businessId);
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
    return this.inventoryService.updateUnit(id, businessId, body, user.sub);
  }

  // ─── Products ────────────────────────────────────────────────────────────────

  @Post('products')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear producto' })
  createProduct(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createProduct(businessId, body, user.sub);
  }

  @Get('products')
  @ApiOperation({ summary: 'Listar productos del negocio' })
  listProducts(
    @BusinessId() businessId: string,
    @Query('categoryId') categoryId?: string,
    @Query('brandId') brandId?: string,
    @Query('search') search?: string,
  ) {
    return this.inventoryService.listProducts(businessId, { categoryId, brandId, search });
  }

  @Get('products/:id')
  @ApiOperation({ summary: 'Obtener producto por ID' })
  findProduct(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.inventoryService.findProduct(id, businessId);
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
    return this.inventoryService.updateProduct(id, businessId, body, user.sub);
  }

  @Delete('products/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar producto' })
  removeProduct(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.inventoryService.removeProduct(id, businessId, user.sub);
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
    return this.inventoryService.addProductUnit(productId, businessId, body, user.sub);
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
    return this.inventoryService.updateProductUnit(productUnitId, productId, businessId, body, user.sub);
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
    return this.inventoryService.removeProductUnit(productUnitId, productId, businessId, user.sub);
  }

  // ─── Warehouses ───────────────────────────────────────────────────────────────

  @Post('warehouses')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Crear almacén' })
  createWarehouse(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createWarehouse(businessId, body, user.sub);
  }

  @Get('warehouses')
  @ApiOperation({ summary: 'Listar almacenes del negocio' })
  listWarehouses(@BusinessId() businessId: string) {
    return this.inventoryService.listWarehouses(businessId);
  }

  @Patch('warehouses/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Actualizar almacén' })
  updateWarehouse(
    @Param('id') id: string,
    @Body() body: any,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.inventoryService.updateWarehouse(id, businessId, body, user.sub);
  }

  @Delete('warehouses/:id')
  @MinRole(MemberRole.MANAGER)
  @ApiOperation({ summary: 'Eliminar almacén' })
  removeWarehouse(
    @Param('id') id: string,
    @BusinessId() businessId: string,
    @User() user: CurrentUser,
  ) {
    return this.inventoryService.removeWarehouse(id, businessId, user.sub);
  }

  // ─── Inputs (Entradas de inventario) ──────────────────────────────────────────

  @Post('inputs')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear entrada de inventario' })
  createInput(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createInput(businessId, body, user.sub);
  }

  @Get('inputs')
  @ApiOperation({ summary: 'Listar entradas de inventario' })
  listInputs(
    @BusinessId() businessId: string,
    @Query('warehouseId') warehouseId?: string,
    @Query('type') type?: string,
    @Query('status') status?: string,
  ) {
    return this.inventoryService.listInputs(businessId, { warehouseId, type, status });
  }

  @Get('inputs/:id')
  @ApiOperation({ summary: 'Obtener entrada por ID' })
  findInput(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.inventoryService.findInput(id, businessId);
  }

  @Post('inputs/:id/confirm')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Confirmar entrada (actualiza stock)' })
  confirmInput(@Param('id') id: string, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.confirmInput(id, businessId, user.sub);
  }

  @Post('inputs/:id/cancel')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Cancelar entrada' })
  cancelInput(@Param('id') id: string, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.cancelInput(id, businessId, user.sub);
  }

  // ─── Outputs (Salidas de inventario / Ventas) ─────────────────────────────────

  @Post('outputs')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear salida de inventario' })
  createOutput(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createOutput(businessId, body, user.sub);
  }

  @Get('outputs')
  @ApiOperation({ summary: 'Listar salidas de inventario' })
  listOutputs(
    @BusinessId() businessId: string,
    @Query('warehouseId') warehouseId?: string,
    @Query('type') type?: string,
    @Query('status') status?: string,
  ) {
    return this.inventoryService.listOutputs(businessId, { warehouseId, type, status });
  }

  @Get('outputs/:id')
  @ApiOperation({ summary: 'Obtener salida por ID' })
  findOutput(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.inventoryService.findOutput(id, businessId);
  }

  @Post('outputs/:id/confirm')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Confirmar salida (descuenta stock)' })
  confirmOutput(@Param('id') id: string, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.confirmOutput(id, businessId, user.sub);
  }

  @Post('outputs/:id/cancel')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Cancelar salida' })
  cancelOutput(@Param('id') id: string, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.cancelOutput(id, businessId, user.sub);
  }

  // ─── Transfers (Traspasos) ──────────────────────────────────────────────────

  @Post('transfers')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Crear traspaso entre almacenes' })
  createTransfer(@Body() body: any, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.createTransfer(businessId, body, user.sub);
  }

  @Get('transfers')
  @ApiOperation({ summary: 'Listar traspasos' })
  listTransfers(
    @BusinessId() businessId: string,
    @Query('fromWarehouseId') fromWarehouseId?: string,
    @Query('toWarehouseId') toWarehouseId?: string,
    @Query('status') status?: string,
  ) {
    return this.inventoryService.listTransfers(businessId, { fromWarehouseId, toWarehouseId, status });
  }

  @Get('transfers/:id')
  @ApiOperation({ summary: 'Obtener traspaso por ID' })
  findTransfer(@Param('id') id: string, @BusinessId() businessId: string) {
    return this.inventoryService.findTransfer(id, businessId);
  }

  @Post('transfers/:id/confirm')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Confirmar traspaso (mueve stock)' })
  confirmTransfer(@Param('id') id: string, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.confirmTransfer(id, businessId, user.sub);
  }

  @Post('transfers/:id/cancel')
  @MinRole(MemberRole.INVENTORY)
  @ApiOperation({ summary: 'Cancelar traspaso' })
  cancelTransfer(@Param('id') id: string, @BusinessId() businessId: string, @User() user: CurrentUser) {
    return this.inventoryService.cancelTransfer(id, businessId, user.sub);
  }

  // ─── Stock ────────────────────────────────────────────────────────────────────

  @Get('stock')
  @ApiOperation({ summary: 'Listar stock' })
  listStock(
    @BusinessId() businessId: string,
    @Query('warehouseId') warehouseId?: string,
    @Query('productId') productId?: string,
  ) {
    return this.inventoryService.listStock(businessId, { warehouseId, productId });
  }

  // ─── Kardex ───────────────────────────────────────────────────────────────────

  @Get('kardex')
  @ApiOperation({ summary: 'Listar movimientos kardex' })
  listKardex(
    @BusinessId() businessId: string,
    @Query('productId') productId?: string,
    @Query('warehouseId') warehouseId?: string,
  ) {
    return this.inventoryService.listKardex(businessId, { productId, warehouseId });
  }
}
