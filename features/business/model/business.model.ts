export interface BusinessRequest {
  businessName: string;
  registrationNumber: string;
  businessType: string;
  industry: string;
  address: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
  contactEmail: string;
  contactPhone: string;
  annualRevenue: number;
  employeeCount: number;
  establishedDate: string;
}

export interface Business extends BusinessRequest {
  id: number;
  ownerId: number;
  status: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface BusinessSummary {
  id: number;
  ownerId: number;
  businessName: string;
  industry: string;
  status: string;
}
