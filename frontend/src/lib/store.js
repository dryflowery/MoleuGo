import { writable } from 'svelte/store';
import { OK } from "./httpStatusStore.js";

export let isListVisible = writable(false); // 알고리즘 리스트의 가시성을 관리하는 변수
export let isLoginVisible = writable(false); // 로그인 팝업의 가시성을 관리하는 변수
export let isLogin = writable(false); // 로그인 유무를 관리하는 변수
export let isUserManualVisible = writable(false); // 매뉴얼 팝업의 가시성을 관리하는 변수

export const checkLoginStatus = async () => {
    const response = await fetch('/auth/status', { credentials: 'include' });

    if (response.status === OK) {
      isLogin.set(true);
    }
    else {
      isLogin.set(false);
    }
};