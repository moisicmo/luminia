export interface MetaPagination {
  total: number;
  page: number;
  lastPage: number;
}

export interface BaseResponse<T> extends MetaPagination {
  data: T[];
}

export const InitBaseResponse = <T>(): BaseResponse<T> => ({
  total: 0,
  page: 1,
  lastPage: 0,
  data: [],
});

// Cart / Payment stubs
export interface FormPaymentModel {
  debt: { id: string };
  amount: number;
}
