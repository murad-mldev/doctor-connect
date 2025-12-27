export interface PaginatedResponse<T> {
  data: T[];
  meta: {
    page: number;
    limit: number;
    totalElements: number;
    totalPages: number;
  };
}
