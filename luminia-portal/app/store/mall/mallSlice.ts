import { createSlice, type PayloadAction } from '@reduxjs/toolkit';
import { type BusinessModel, type BusinessCategory, BusinessType } from '@/models';

interface MallState {
  businesses: BusinessModel[];
  filteredBusinesses: BusinessModel[];
  categories: BusinessCategory[];
  selectedType: BusinessType | null;
  selectedCity: string | null;
  searchQuery: string;
  loading: boolean;
}

const initialState: MallState = {
  businesses: [],
  filteredBusinesses: [],
  categories: [],
  selectedType: null,
  selectedCity: null,
  searchQuery: '',
  loading: false,
};

export const mallSlice = createSlice({
  name: 'mall',
  initialState,
  reducers: {
    setBusinesses: (state, { payload }: PayloadAction<BusinessModel[]>) => {
      state.businesses = payload;
      state.filteredBusinesses = applyFilters(payload, state.selectedType, state.selectedCity, state.searchQuery);
    },
    setCategories: (state, { payload }: PayloadAction<BusinessCategory[]>) => {
      state.categories = payload;
    },
    setSelectedType: (state, { payload }: PayloadAction<BusinessType | null>) => {
      state.selectedType = payload;
      state.filteredBusinesses = applyFilters(state.businesses, payload, state.selectedCity, state.searchQuery);
    },
    setSelectedCity: (state, { payload }: PayloadAction<string | null>) => {
      state.selectedCity = payload;
      state.filteredBusinesses = applyFilters(state.businesses, state.selectedType, payload, state.searchQuery);
    },
    setSearchQuery: (state, { payload }: PayloadAction<string>) => {
      state.searchQuery = payload;
      state.filteredBusinesses = applyFilters(state.businesses, state.selectedType, state.selectedCity, payload);
    },
    setLoading: (state, { payload }: PayloadAction<boolean>) => {
      state.loading = payload;
    },
  },
});

function applyFilters(
  businesses: BusinessModel[],
  type: BusinessType | null,
  city: string | null,
  query: string,
): BusinessModel[] {
  return businesses.filter((b) => {
    const matchType = !type || b.type === type;
    const matchCity = !city || b.city?.toLowerCase().includes(city.toLowerCase());
    const matchQuery = !query || b.name.toLowerCase().includes(query.toLowerCase()) ||
      b.description?.toLowerCase().includes(query.toLowerCase()) ||
      b.category?.toLowerCase().includes(query.toLowerCase());
    return matchType && matchCity && matchQuery;
  });
}

export const { setBusinesses, setCategories, setSelectedType, setSelectedCity, setSearchQuery, setLoading } =
  mallSlice.actions;
