import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import * as soap from 'soap';
import { envs } from '../../config';
import { SIAT_WSDL } from './siat.constants';

@Injectable()
export class SiatSoapService implements OnModuleInit {
  private readonly logger = new Logger(SiatSoapService.name);
  private clients: Map<string, soap.Client> = new Map();

  async onModuleInit() {
    await this.initClients();
  }

  private async initClients() {
    const baseUrl = envs.siat.soapUrl;
    const options: soap.IOptions = {
      forceSoap12Headers: false,
    };

    for (const [key, wsdlPath] of Object.entries(SIAT_WSDL)) {
      try {
        const wsdlUrl = `${baseUrl}${wsdlPath}`;
        const client = await soap.createClientAsync(wsdlUrl, options);
        this.clients.set(key, client);
        this.logger.log(`SIAT client initialized: ${key}`);
      } catch (err) {
        this.logger.warn(`Could not init SIAT client ${key}: ${err.message}`);
      }
    }
  }

  getClient(service: keyof typeof SIAT_WSDL): soap.Client | undefined {
    return this.clients.get(service);
  }

  async call<T = any>(
    service: keyof typeof SIAT_WSDL,
    method: string,
    args: Record<string, any>,
  ): Promise<T> {
    const client = this.getClient(service);
    if (!client) {
      throw new Error(`SIAT client not available: ${service}`);
    }

    try {
      const [result] = await client[`${method}Async`](args);
      return result as T;
    } catch (err) {
      this.logger.error(`SIAT call failed [${service}.${method}]: ${err.message}`);
      throw err;
    }
  }

  // ─── Operaciones (cancellation / reversal / status) ──────────────────────

  async recepcionFactura(args: {
    SolicitudServicioRecepcionFactura: any;
  }) {
    return this.call('COMPRA_VENTA', 'recepcionFactura', args);
  }

  async recepcionMasivaFactura(args: { SolicitudServicioRecepcionMasiva: any }) {
    return this.call('COMPRA_VENTA', 'recepcionMasivaFactura', args);
  }

  async recepcionPaqueteFactura(args: { SolicitudServicioRecepcionPaquete: any }) {
    return this.call('COMPRA_VENTA', 'recepcionPaqueteFactura', args);
  }

  async validacionRecepcionMasivaFactura(args: {
    SolicitudServicioValidacionRecepcionMasiva: any;
  }) {
    return this.call('COMPRA_VENTA', 'validacionRecepcionMasivaFactura', args);
  }

  async verificacionEstadoFactura(args: {
    SolicitudServicioVerificacionEstadoFactura: any;
  }) {
    return this.call('COMPRA_VENTA', 'verificacionEstadoFactura', args);
  }

  async anulacionFactura(args: { SolicitudServicioAnulacionFactura: any }) {
    return this.call('OPERACIONES', 'anulacionFactura', args);
  }

  async reversionAnulacionFactura(args: {
    SolicitudServicioReversionAnulacionFactura: any;
  }) {
    return this.call('OPERACIONES', 'reversionAnulacionFactura', args);
  }

  async verificacionNit(args: { SolicitudServicioVerificacionNit: any }) {
    return this.call('OPERACIONES', 'verificacionNit', args);
  }

  // ─── Sincronizacion ────────────────────────────────────────────────────────

  async solicitarCuis(args: { SolicitudCuis: any }) {
    return this.call('SINCRONIZACION', 'solicitarCuis', args);
  }

  async solicitarCufd(args: { SolicitudCufd: any }) {
    return this.call('SINCRONIZACION', 'solicitarCufd', args);
  }

  // ─── Codigos (master data) ─────────────────────────────────────────────────

  async sincronizarActividades(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarActividades', args);
  }

  async sincronizarListaProductosServicios(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarListaProductosServicios', args);
  }

  async sincronizarParametricaUnidadMedida(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaUnidadMedida', args);
  }

  async sincronizarParametricaTipoDocumentoIdentidad(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call(
      'CODIGOS',
      'sincronizarParametricaTipoDocumentoIdentidad',
      args,
    );
  }

  async sincronizarParametricaTipoDocumentoSector(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call(
      'CODIGOS',
      'sincronizarParametricaTipoDocumentoSector',
      args,
    );
  }

  async sincronizarParametricaTipoMoneda(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaTipoMoneda', args);
  }

  async sincronizarParametricaTipoMetodoPago(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaTipoMetodoPago', args);
  }

  async sincronizarParametricaTipoFactura(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaTipoFactura', args);
  }

  async sincronizarParametricaEventoSignificativo(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call(
      'CODIGOS',
      'sincronizarParametricaEventoSignificativo',
      args,
    );
  }

  async sincronizarParametricaMotivoAnulacion(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaMotivoAnulacion', args);
  }

  async sincronizarParametricaPaisOrigen(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaPaisOrigen', args);
  }

  async sincronizarParametricaTipoEmision(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaTipoEmision', args);
  }

  async sincronizarParametricaLeyendaFactura(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaLeyendaFactura', args);
  }

  async sincronizarParametricaMensajeServicio(args: {
    SolicitudSincronizacion: any;
  }) {
    return this.call('CODIGOS', 'sincronizarParametricaMensajeServicio', args);
  }
}
