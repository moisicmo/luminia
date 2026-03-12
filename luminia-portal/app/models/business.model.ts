import type { BusinessType } from './enums';

export interface BusinessModel {
  id: string;
  name: string;
  description?: string;
  logoUrl?: string;
  coverUrl?: string;
  type: BusinessType;
  category?: string;
  city?: string;
  address?: string;
  phone?: string;
  email?: string;
  website?: string;
  isOpen?: boolean;
  isFeatured?: boolean;
  isNew?: boolean;
  rating?: number;
  reviewCount?: number;
  tags?: string[];
}

export interface BusinessCategory {
  id: string;
  label: string;
  type: BusinessType;
  icon: string;
  color: string;
  count?: number;
}
