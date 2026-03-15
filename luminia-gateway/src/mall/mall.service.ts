import { Inject, Injectable, HttpException, HttpStatus, Logger } from '@nestjs/common';
import { ClientProxy } from '@nestjs/microservices';
import { firstValueFrom, timeout, TimeoutError } from 'rxjs';
import { RMQServiceInventories, RMQServiceBusinesses } from '@/config';

const RMQ_TIMEOUT_MS = 10_000;

@Injectable()
export class MallService {
  private readonly logger = new Logger(MallService.name);

  constructor(
    @Inject(RMQServiceInventories.getName())
    private readonly inventoryClient: ClientProxy,
    @Inject(RMQServiceBusinesses.getName())
    private readonly businessClient: ClientProxy,
  ) {}

  private async send<T>(client: ClientProxy, pattern: string, payload: unknown): Promise<T> {
    this.logger.log(`[RMQ] enviando → ${pattern}`);
    try {
      const result = await firstValueFrom(
        client.send<T>(pattern, payload).pipe(timeout(RMQ_TIMEOUT_MS)),
      );
      this.logger.log(`[RMQ] respuesta OK ← ${pattern}`);
      return result;
    } catch (err) {
      if (err instanceof TimeoutError) {
        this.logger.error(`[RMQ] TIMEOUT: ${pattern}`);
        throw new HttpException(
          `El servicio no respondió a tiempo (${pattern})`,
          HttpStatus.GATEWAY_TIMEOUT,
        );
      }
      this.logger.error(`[RMQ] ERROR en ${pattern}: ${JSON.stringify(err)}`);
      throw err;
    }
  }

  async listMallProducts(query: { search?: string; businessType?: string; businessId?: string; take?: number; skip?: number }) {
    let businessIds: string[] | undefined;

    // If filtering by a specific business
    if (query.businessId) {
      businessIds = [query.businessId];
    }

    // If filtering by businessType, get matching business IDs first
    if (!businessIds && query.businessType) {
      const businesses = await this.send<any[]>(
        this.businessClient,
        'business.list.public',
        {},
      );
      businessIds = businesses
        .filter((b: any) => b.businessType === query.businessType)
        .map((b: any) => b.id);

      if (businessIds.length === 0) {
        return { items: [], total: 0 };
      }
    }

    // Get products from inventory
    const result = await this.send<{ items: any[]; total: number }>(
      this.inventoryClient,
      'store.products.list.mall',
      {
        businessIds,
        search: query.search,
        take: query.take,
        skip: query.skip,
      },
    );

    // Enrich products with business info
    const allBusinesses = await this.send<any[]>(
      this.businessClient,
      'business.list.public',
      {},
    );
    const bizMap = new Map(allBusinesses.map((b: any) => [b.id, b]));

    const items = result.items.map((p: any) => {
      const biz = bizMap.get(p.businessId);
      return {
        ...p,
        businessName: biz?.name ?? '',
        businessLogo: biz?.logo ?? null,
        businessSlug: biz?.url ?? null,
      };
    });

    return { items, total: result.total };
  }
}
