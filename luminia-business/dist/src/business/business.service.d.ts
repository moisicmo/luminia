import { CreateBusinessDto } from './dto/create-business.dto';
export declare class BusinessService {
    private readonly logger;
    create(dto: CreateBusinessDto, ownerId: string): Promise<{
        businessId: any;
        branchId: any;
        pointOfSaleId: any;
        name: any;
        businessType: any;
    }>;
    findByOwner(ownerId: string): Promise<runtime.Types.Public.PrismaPromise<T>>;
    findOne(businessId: string, requesterId: string): Promise<any>;
}
