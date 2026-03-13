import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { InventoryService } from './inventory.service';

@Controller()
export class InventoryController {
  constructor(private readonly inventoryService: InventoryService) {}

  // ─── Warehouses ──────────────────────────────────────────────────────────────

  @MessagePattern('store.warehouses.create')
  createWarehouse(@Payload() d: { data: any; createdBy: string }) {
    return this.inventoryService.createWarehouse(d.data, d.createdBy);
  }

  @MessagePattern('store.warehouses.list')
  listWarehouses(@Payload() d: { businessId: string }) {
    return this.inventoryService.listWarehouses(d.businessId);
  }

  @MessagePattern('store.warehouses.update')
  updateWarehouse(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.inventoryService.updateWarehouse(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.warehouses.remove')
  removeWarehouse(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.inventoryService.removeWarehouse(d.id, d.businessId, d.updatedBy);
  }

  // ─── Inputs ──────────────────────────────────────────────────────────────────

  @MessagePattern('store.inputs.create')
  createInput(@Payload() d: { data: any; createdBy: string }) {
    return this.inventoryService.createInput(d.data, d.createdBy);
  }

  @MessagePattern('store.inputs.list')
  listInputs(@Payload() d: { businessId: string; filters: any }) {
    return this.inventoryService.listInputs(d.businessId, d.filters ?? {});
  }

  @MessagePattern('store.inputs.findOne')
  findInput(@Payload() d: { id: string; businessId: string }) {
    return this.inventoryService.findInput(d.id, d.businessId);
  }

  @MessagePattern('store.inputs.confirm')
  confirmInput(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.inventoryService.confirmInput(d.id, d.businessId, d.updatedBy);
  }

  @MessagePattern('store.inputs.cancel')
  cancelInput(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.inventoryService.cancelInput(d.id, d.businessId, d.updatedBy);
  }

  // ─── Customers ────────────────────────────────────────────────────────────────

  @MessagePattern('store.customers.create')
  createCustomer(@Payload() d: { data: any; createdBy: string }) {
    return this.inventoryService.createCustomer(d.data, d.createdBy);
  }

  @MessagePattern('store.customers.list')
  listCustomers(@Payload() d: { businessId: string }) {
    return this.inventoryService.listCustomers(d.businessId);
  }

  @MessagePattern('store.customers.update')
  updateCustomer(@Payload() d: { id: string; businessId: string; data: any; updatedBy: string }) {
    return this.inventoryService.updateCustomer(d.id, d.businessId, d.data, d.updatedBy);
  }

  @MessagePattern('store.customers.remove')
  removeCustomer(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.inventoryService.removeCustomer(d.id, d.businessId, d.updatedBy);
  }

  // ─── Outputs ─────────────────────────────────────────────────────────────────

  @MessagePattern('store.outputs.create')
  createOutput(@Payload() d: { data: any; createdBy: string }) {
    return this.inventoryService.createOutput(d.data, d.createdBy);
  }

  @MessagePattern('store.outputs.list')
  listOutputs(@Payload() d: { businessId: string; filters: any }) {
    return this.inventoryService.listOutputs(d.businessId, d.filters ?? {});
  }

  @MessagePattern('store.outputs.confirm')
  confirmOutput(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.inventoryService.confirmOutput(d.id, d.businessId, d.updatedBy);
  }

  @MessagePattern('store.outputs.cancel')
  cancelOutput(@Payload() d: { id: string; businessId: string; updatedBy: string }) {
    return this.inventoryService.cancelOutput(d.id, d.businessId, d.updatedBy);
  }

  // ─── Stock ───────────────────────────────────────────────────────────────────

  @MessagePattern('store.stock.list')
  listStock(@Payload() d: { businessId: string; filters: any }) {
    return this.inventoryService.listStock(d.businessId, d.filters ?? {});
  }

  // ─── Kardex ──────────────────────────────────────────────────────────────────

  @MessagePattern('store.kardex.list')
  listKardex(@Payload() d: { businessId: string; filters: any }) {
    return this.inventoryService.listKardex(d.businessId, d.filters ?? {});
  }
}
