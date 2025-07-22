import {
    EXPERIENCE_LOADING,
    FETCH_EXPERIENCES,
    FETCH_SINGLE_EXPERIENCE,
    EXPERIENCE_ERROR,
} from "../actions/index";

const initialState = {
    experiences: [],
    singleExperience: null,
    pageNumber: 0,
    pageSize: 10,
    totalElements: 0,
    totalPages: 0,
    lastPage: false,
    isLoading: false,
    errorMessage: null,
};

export const experienceReducer = (state = initialState, action) => {
    switch (action.type) {
        case EXPERIENCE_LOADING:
            return { ...state, isLoading: true, errorMessage: null };

        case FETCH_EXPERIENCES:
            return {
                ...state,
                isLoading: false,
                experiences: action.payload.experiences,
                pageNumber: action.payload.pageNumber,
                pageSize: action.payload.pageSize,
                totalElements: action.payload.totalElements,
                totalPages: action.payload.totalPages,
                lastPage: action.payload.lastPage,
            };

        case FETCH_SINGLE_EXPERIENCE:
            return {
                ...state,
                isLoading: false,
                singleExperience: action.payload,
                errorMessage: null,
            };

        case EXPERIENCE_ERROR:
            return { ...state, isLoading: false, errorMessage: action.payload };

        default:
            return state;
    }
};