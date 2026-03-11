import { Injectable, Logger } from '@nestjs/common';
import * as forge from 'node-forge';
import * as crypto from 'node:crypto';
import { SignedXml } from 'xml-crypto';
import { DOMParser, XMLSerializer } from '@xmldom/xmldom';

@Injectable()
export class SignatureService {
  private readonly logger = new Logger(SignatureService.name);

  /**
   * Signs an XML string with the given PEM private key and X.509 certificate.
   * Uses RSA-SHA256 with enveloped signature, matching the Java implementation.
   */
  signXml(xmlString: string, pemPrivateKey: string, pemCertificate: string): string {
    const doc = new DOMParser().parseFromString(xmlString, 'text/xml');

    const sig = new SignedXml({
      privateKey: pemPrivateKey,
      publicCert: this.extractCertificate(pemCertificate),
    });

    sig.addReference({
      xpath: '/*',
      digestAlgorithm: 'http://www.w3.org/2001/04/xmlenc#sha256',
      transforms: [
        'http://www.w3.org/2000/09/xmldsig#enveloped-signature',
        'http://www.w3.org/2001/10/xml-exc-c14n#',
      ],
    });

    sig.canonicalizationAlgorithm = 'http://www.w3.org/2001/10/xml-exc-c14n#';
    sig.signatureAlgorithm =
      'http://www.w3.org/2001/04/xmldsig-more#rsa-sha256';

    sig.computeSignature(xmlString, {
      location: { reference: '/*', action: 'append' },
    });

    return sig.getSignedXml();
  }

  /**
   * Generates the SHA-256 hash of the given content (for CUF generation).
   */
  sha256(content: string): string {
    return crypto.createHash('sha256').update(content, 'utf8').digest('hex');
  }

  /**
   * Compress a string with GZIP (base64 output), matching Java's GZIPOutputStream.
   */
  async gzipBase64(data: string): Promise<string> {
    const { gzip } = await import('node:zlib');
    const { promisify } = await import('node:util');
    const gzipAsync = promisify(gzip);
    const compressed = await gzipAsync(Buffer.from(data, 'utf8'));
    return compressed.toString('base64');
  }

  /**
   * Validates the certificate and returns its common fields.
   */
  parseCertificate(pemCertificate: string): {
    subject: string;
    validFrom: Date;
    validTo: Date;
  } {
    const cert = forge.pki.certificateFromPem(pemCertificate);
    return {
      subject: cert.subject.getField('CN')?.value ?? '',
      validFrom: cert.validity.notBefore,
      validTo: cert.validity.notAfter,
    };
  }

  /**
   * Checks if a certificate is valid (not expired) within the given days threshold.
   */
  isCertificateExpiringSoon(pemCertificate: string, thresholdDays = 30): boolean {
    const { validTo } = this.parseCertificate(pemCertificate);
    const now = new Date();
    const diff = (validTo.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
    return diff <= thresholdDays;
  }

  private extractCertificate(pem: string): string {
    return pem
      .replace(/-----BEGIN CERTIFICATE-----/g, '')
      .replace(/-----END CERTIFICATE-----/g, '')
      .replace(/\s/g, '');
  }
}
