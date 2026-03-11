import 'dotenv/config';
import * as joi from 'joi';

interface EnvVars {
  PORT: number;
  RMQ_SERVERS: string[];
  JWT_SECRET: string;
  JWT_EXPIRES_IN: string;
}

const envsSchema = joi
  .object({
    PORT: joi.number().required(),
    RMQ_SERVERS: joi.array().items(joi.string()).required(),
    JWT_SECRET: joi.string().required(),
    JWT_EXPIRES_IN: joi.string().default('7d'),
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
};
