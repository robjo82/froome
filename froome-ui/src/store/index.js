import { createStore } from 'vuex';
import axios from 'axios';

const store = createStore({
    state: {
        user: null
    },
    mutations: {
        setUser(state, user) {
            state.user = user;
        },
        clearUser(state) {
            state.user = null;
        }
    },
    actions: {
        fetchUser({ commit }) {
            const bearerToken = localStorage.getItem('bearerToken');
            if (bearerToken) {
                axios.get(`${import.meta.env.VITE_USER_API_URL}/api/users/me`, {
                    headers: { 'Authorization': `Bearer ${bearerToken}` }
                })
                    .then(response => {
                        commit('setUser', response.data);
                    })
                    .catch(() => {
                        commit('clearUser');
                    });
            } else {
                commit('clearUser');
            }
        },
        logout({ commit }) {
            localStorage.removeItem('bearerToken');
            commit('clearUser');
        }
    },
    getters: {
        isAdmin: state => state.user && state.user.admin,
        isLoggedIn: state => !!state.user
    }
});

export default store;
