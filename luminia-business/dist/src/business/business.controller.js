"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
var _a, _b;
Object.defineProperty(exports, "__esModule", { value: true });
exports.BusinessController = void 0;
const common_1 = require("@nestjs/common");
const microservices_1 = require("@nestjs/microservices");
const business_service_1 = require("./business.service");
const member_service_1 = require("./member.service");
let BusinessController = class BusinessController {
    businessService;
    memberService;
    constructor(businessService, memberService) {
        this.businessService = businessService;
        this.memberService = memberService;
    }
    create(data) {
        return this.businessService.create(data.dto, data.ownerId);
    }
    findAll(data) {
        return this.businessService.findByOwner(data.ownerId);
    }
    findOne(data) {
        return this.businessService.findOne(data.businessId, data.requesterId);
    }
    addMember(data) {
        return this.memberService.add(data.businessId, data.userId, data.role, data.invitedBy);
    }
    listMembers(data) {
        return this.memberService.list(data.businessId, data.requesterId);
    }
    updateMemberRole(data) {
        return this.memberService.updateRole(data.businessId, data.memberId, data.role, data.requesterId);
    }
    removeMember(data) {
        return this.memberService.remove(data.businessId, data.memberId, data.requesterId);
    }
    checkAccess(data) {
        return this.memberService.checkAccess(data.businessId, data.userId);
    }
};
exports.BusinessController = BusinessController;
__decorate([
    (0, microservices_1.MessagePattern)('business.create'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "create", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.list'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "findAll", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.findOne'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "findOne", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.members.add'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "addMember", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.members.list'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "listMembers", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.members.updateRole'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "updateMemberRole", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.members.remove'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "removeMember", null);
__decorate([
    (0, microservices_1.MessagePattern)('business.access.check'),
    __param(0, (0, microservices_1.Payload)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], BusinessController.prototype, "checkAccess", null);
exports.BusinessController = BusinessController = __decorate([
    (0, common_1.Controller)(),
    __metadata("design:paramtypes", [typeof (_a = typeof business_service_1.BusinessService !== "undefined" && business_service_1.BusinessService) === "function" ? _a : Object, typeof (_b = typeof member_service_1.MemberService !== "undefined" && member_service_1.MemberService) === "function" ? _b : Object])
], BusinessController);
//# sourceMappingURL=business.controller.js.map