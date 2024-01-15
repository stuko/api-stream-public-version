import data from "MOCK_DATA.json";

const SET_PRODUCTS = "SET_PRODUCTS";
function setProducts(items) {
  return {
    type: SET_PRODUCTS,
    items
  };
}

function getProducts() {
  return (dispatch, getState) => {
    fetch("MOCK_DATA.json", {
      headers : { 
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
   })
    .then(response => data)
    .then(json => dispatch(setProducts(json)))
    .catch(err => console.log(err));
  };
}


const initialState = {};

function reducer(state = initialState, action) {
  switch (action.type) {
    case SET_PRODUCTS:
      return applySetProducts(state, action);
    default:
      return state;
  }
}

function applySetProducts(state, action) {
  const { items } = action;
  return {
    ...state,
    items
  };
}

const actionCreators = {
  getProducts
};

export { actionCreators };

export default reducer;