import 'dotenv/config';
import * as joi from 'joi';

interface EnvVars {
  PORT: number;
  RMQ_SERVERS: string[];
  JWT_SECRET: string;
  JWT_EXPIRES_IN: string;
  OPENAI_API_KEY: string;
  DEEPSEEK_API_KEY: string;
  DEEPSEEK_BASE_URL: string;
  WHATSAPP_VERIFY_TOKEN: string;
  DEFAULT_AI_PROVIDER: string;
  DEFAULT_AI_MODEL: string;
}

const envsSchema = joi
  .object({
    PORT: joi.number().required(),
    RMQ_SERVERS: joi.array().items(joi.string()).required(),
    JWT_SECRET: joi.string().required(),
    JWT_EXPIRES_IN: joi.string().default('7d'),
    OPENAI_API_KEY: joi.string().required(),
    DEEPSEEK_API_KEY: joi.string().optional().default(''),
    DEEPSEEK_BASE_URL: joi.string().optional().default('https://api.deepseek.com/v1'),
    WHATSAPP_VERIFY_TOKEN: joi.string().optional().default('luminia_verify_token'),
    DEFAULT_AI_PROVIDER: joi.string().optional().default('OPENAI'),
    DEFAULT_AI_MODEL: joi.string().optional().default('gpt-4o-mini'),
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
  openaiApiKey: envVars.OPENAI_API_KEY,
  deepseekApiKey: envVars.DEEPSEEK_API_KEY,
  deepseekBaseUrl: envVars.DEEPSEEK_BASE_URL,
  whatsappVerifyToken: envVars.WHATSAPP_VERIFY_TOKEN,
  defaultAiProvider: envVars.DEFAULT_AI_PROVIDER,
  defaultAiModel: envVars.DEFAULT_AI_MODEL,
};
