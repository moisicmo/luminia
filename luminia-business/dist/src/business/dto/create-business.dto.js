"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.CreateBusinessDto = exports.CreateBranchDto = void 0;
class CreateBranchDto {
    name;
    region;
    address;
    municipality;
    phone;
    latitude;
    longitude;
    openingHours;
}
exports.CreateBranchDto = CreateBranchDto;
class CreateBusinessDto {
    name;
    businessType;
    taxId;
    url;
    branch;
}
exports.CreateBusinessDto = CreateBusinessDto;
//# sourceMappingURL=create-business.dto.js.map