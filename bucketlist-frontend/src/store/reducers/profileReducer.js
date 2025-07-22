const initialState = {
    createdExperiences: [],
    bucketList: [],
};

export const profileReducer = (state = initialState, action) => {
    switch (action.type) {
        case "FETCH_USER_EXPERIENCES":
            return { ...state, createdExperiences: action.payload };
        case "FETCH_USER_BUCKETLIST":
            return { ...state, bucketList: action.payload };
        default:
            return state;
    }
};
