import { Controller } from '@nestjs/common';
import { MessagePattern, Payload } from '@nestjs/microservices';
import { PersonService } from './person.service';
import { CreatePersonDto } from './dto/create-person.dto';

@Controller()
export class PersonController {
  constructor(private readonly personService: PersonService) {}

  @MessagePattern('person.search')
  search(@Payload() payload: { q: string }) {
    return this.personService.search(payload.q);
  }

  @MessagePattern('person.create')
  create(@Payload() payload: { dto: CreatePersonDto; createdBy: string }) {
    return this.personService.create(payload.dto, payload.createdBy);
  }
}
