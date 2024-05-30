import { createRouter, createWebHistory } from 'vue-router';
import Login from '../components/pages/Login.vue';
import Logout from '../components/pages/Logout.vue';
import Register from '../components/pages/Register.vue';
import Account from '../components/pages/Account.vue';
import Basket from '../components/pages/Basket.vue';
import Orders from '../components/pages/Orders.vue';
import Admin from '../components/pages/Admin.vue';
import Home from "../components/pages/Home.vue";
import Payment from "../components/pages/Payment.vue";

const routes = [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/logout', component: Logout },
    { path: '/register', component: Register },
    { path: '/account', component: Account },
    { path: '/basket', component: Basket },
    { path: '/orders', component: Orders },
    { path: '/admin', component: Admin },
    { path: '/payment/:orderId', component: Payment },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
});

router.beforeEach((to, from, next) => {
    const publicPages = ['/login', '/register'];
    const authRequired = !publicPages.includes(to.path);
    const loggedIn = localStorage.getItem('bearerToken');

    if (authRequired && !loggedIn) {
        return next('/login');
    }

    next();
});

export default router;
