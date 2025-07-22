const initialState = {
    bucketListExps: [],
    bucketListId: null,
};

export const bucketListReducer = (state = initialState, action) => {
    switch (action.type) {
        case "ADD_BUCKETLIST_EXP": {
            const expToAdd = action.payload;
            const existingExp = state.bucketListExps.find(
                (item) => item.productId === expToAdd.productId
            );

            if (existingExp) {
                const updatedExps = state.bucketListExps.map((item) =>
                    item.productId === expToAdd.productId ? expToAdd : item
                );

                return {
                    ...state,
                    bucketListExps: updatedExps,
                };
            } else {
                return {
                    ...state,
                    bucketListExps: [...state.bucketListExps, expToAdd],
                };
            }
        }

        case "REMOVE_BUCKETLIST_EXP":
            return {
                ...state,
                bucketListExps: state.bucketListExps.filter(
                    (item) => item.productId !== action.payload.productId
                ),
            };

        case "GET_USER_BUCKETLIST_EXPS":
            return {
                ...state,
                bucketListExps: action.payload,
                bucketListId: action.bucketListId,
            };

        case "CLEAR_BUCKETLIST":
            return {
                bucketListExps: [],
                bucketListId: null,
            };

        default:
            return state;
    }
};
