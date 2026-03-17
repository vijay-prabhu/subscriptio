export interface Tenant {
  id: string;
  name: string;
  slug: string;
  status: string;
  createdAt: string;
}

export interface Plan {
  id: string;
  name: string;
  description: string;
  price: number;
  currency: string;
  billingInterval: string;
  trialDays: number;
  features: string;
  isActive: boolean;
  createdAt: string;
}

export interface Customer {
  id: string;
  email: string;
  name: string;
  createdAt: string;
}

export interface Subscription {
  id: string;
  status: string;
  plan: Plan;
  customerId: string;
  currentPeriodStart: string;
  currentPeriodEnd: string;
  trialEnd: string | null;
  cancelAtPeriodEnd: boolean;
  cancelledAt: string | null;
  createdAt: string;
}

export interface Invoice {
  id: string;
  invoiceNumber: string;
  status: string;
  subtotal: number;
  tax: number;
  total: number;
  currency: string;
  dueDate: string;
  paidAt: string | null;
  periodStart: string;
  periodEnd: string;
  pdfUrl: string | null;
  lineItems: LineItem[];
  createdAt: string;
}

export interface LineItem {
  description: string;
  quantity: number;
  unitPrice: number;
  amount: number;
}

export interface DashboardMetrics {
  mrr: number;
  activeSubscriptions: number;
  trialingSubscriptions: number;
  pastDueSubscriptions: number;
  totalInvoices: number;
  revenueByPlan: { plan: string; revenue: number; count: number }[];
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  email: string;
  role: string;
}
