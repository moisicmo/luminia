export declare const MemberRole: {
    readonly OWNER: "OWNER";
    readonly ADMIN: "ADMIN";
    readonly MANAGER: "MANAGER";
    readonly SELLER: "SELLER";
    readonly INVENTORY: "INVENTORY";
    readonly VIEWER: "VIEWER";
};
export type MemberRole = (typeof MemberRole)[keyof typeof MemberRole];
export declare const BusinessType: {
    readonly ADMINISTRACION: "ADMINISTRACION";
    readonly SERVICIO: "SERVICIO";
    readonly TIENDA: "TIENDA";
    readonly RESTAURANT: "RESTAURANT";
    readonly HOTEL: "HOTEL";
    readonly CAPACITACION: "CAPACITACION";
    readonly INSTITUTO: "INSTITUTO";
    readonly COLEGIO: "COLEGIO";
    readonly TRANSPORTE: "TRANSPORTE";
    readonly SALUD: "SALUD";
    readonly ENTRETENIMIENTO: "ENTRETENIMIENTO";
    readonly ECOMMERCE: "ECOMMERCE";
    readonly CONSULTORIA: "CONSULTORIA";
};
export type BusinessType = (typeof BusinessType)[keyof typeof BusinessType];
