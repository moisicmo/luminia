import { Injectable, Logger } from '@nestjs/common';
import { SearchProductsTool } from './search-products.tool';
import { CheckStockTool } from './check-stock.tool';
import { CreateOrderTool } from './create-order.tool';
import type { ToolDefinition } from '@/ai-providers/interfaces/ai-provider.interface';

interface RegisteredTool {
  definition: ToolDefinition;
  execute: (args: any, businessId: string) => Promise<any>;
}

@Injectable()
export class ToolRegistryService {
  private readonly logger = new Logger(ToolRegistryService.name);
  private readonly tools: Map<string, RegisteredTool>;

  constructor(
    private readonly searchProducts: SearchProductsTool,
    private readonly checkStock: CheckStockTool,
    private readonly createOrder: CreateOrderTool,
  ) {
    this.tools = new Map([
      ['searchProducts', { definition: this.searchProducts.definition, execute: (a, b) => this.searchProducts.execute(a, b) }],
      ['checkStock', { definition: this.checkStock.definition, execute: (a, b) => this.checkStock.execute(a, b) }],
      ['createOrder', { definition: this.createOrder.definition, execute: (a, b) => this.createOrder.execute(a, b) }],
    ]);
  }

  getDefinitions(enabledTools?: string[]): ToolDefinition[] {
    if (!enabledTools?.length) return [...this.tools.values()].map((t) => t.definition);
    return enabledTools
      .filter((name) => this.tools.has(name))
      .map((name) => this.tools.get(name)!.definition);
  }

  async execute(toolName: string, args: any, businessId: string): Promise<any> {
    const tool = this.tools.get(toolName);
    if (!tool) {
      this.logger.warn(`Tool not found: ${toolName}`);
      return { error: `Herramienta no encontrada: ${toolName}` };
    }
    this.logger.log(`Executing tool: ${toolName}`);
    return tool.execute(args, businessId);
  }
}
