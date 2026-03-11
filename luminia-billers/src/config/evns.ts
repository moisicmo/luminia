import 'dotenv/config';
import * as joi from 'joi';

interface EnvVars {
  PORT: number;
  RMQ_SERVERS: string[];
  JWT_SECRET: string;
  JWT_EXPIRES_IN: string;
  DATABASE_URL: string;
  SIAT_SOAP_URL: string;
  SIAT_TIMEOUT_CONNECT: number;
  SIAT_TIMEOUT_REQUEST: number;
  SIAT_QR_URL: string;
  INVOICE_TEMP_FOLDER: string;
  INVOICE_NUMBER_LIMIT_BATCH: number;
  CANCELLATION_TERM_DAYS: number;
}

const envsSchema = joi
  .object({
    PORT: joi.number().required(),
    RMQ_SERVERS: joi.array().items(joi.string()).required(),
    JWT_SECRET: joi.string().required(),
    JWT_EXPIRES_IN: joi.string().default('7d'),
    DATABASE_URL: joi.string().required(),
    SIAT_SOAP_URL: joi
      .string()
      .default('https://pilotosiatservicios.impuestos.gob.bo/v2/'),
    SIAT_TIMEOUT_CONNECT: joi.number().default(500),
    SIAT_TIMEOUT_REQUEST: joi.number().default(15000),
    SIAT_QR_URL: joi
      .string()
      .default(
        'https://pilotosiat.impuestos.gob.bo/consulta/QR?nit=%s&cuf=%s&numero=%s&t=0',
      ),
    INVOICE_TEMP_FOLDER: joi.string().default('/tmp/invoice/'),
    INVOICE_NUMBER_LIMIT_BATCH: joi.number().default(500),
    CANCELLATION_TERM_DAYS: joi.number().default(9),
  })
  .unknown(true);

const { error, value } = envsSchema.validate({
  ...process.env,
  RMQ_SERVERS: process.env.RMQ_SERVERS?.split(','),
});

if (error) {
  throw new Error(`Config validation error: ${error.message}`);
}

const envVars: EnvVars = value;

export const envs = {
  port: envVars.PORT,
  rmqServers: envVars.RMQ_SERVERS,
  jwtSecret: envVars.JWT_SECRET,
  jwtExpiresIn: envVars.JWT_EXPIRES_IN,
  databaseUrl: envVars.DATABASE_URL,
  siat: {
    soapUrl: envVars.SIAT_SOAP_URL,
    timeoutConnect: envVars.SIAT_TIMEOUT_CONNECT,
    timeoutRequest: envVars.SIAT_TIMEOUT_REQUEST,
    qrUrl: envVars.SIAT_QR_URL,
  },
  invoice: {
    tempFolder: envVars.INVOICE_TEMP_FOLDER,
    numberLimitBatch: envVars.INVOICE_NUMBER_LIMIT_BATCH,
    cancellationTermDays: envVars.CANCELLATION_TERM_DAYS,
  },
};
