const initialState = {
    categories: [],
    isLoading: false,
    errorMessage: null,
};

export const categoryReducer = (state = initialState, action) => {
    switch (action.type) {
        case "CATEGORY_LOADER":
            return { ...state, isLoading: true, errorMessage: null };

        case "FETCH_CATEGORIES":
            return {
                ...state,
                isLoading: false,
                categories: action.payload,
            };

        case "IS_ERROR":
            return {
                ...state,
                isLoading: false,
                errorMessage: action.payload,
            };

        default:
            return state;
    }
};
