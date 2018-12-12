package com.redditgifts.mobile.models

interface LoginViewModelInputs

interface LoginViewModelOutputs

class LoginViewModel : DisposableViewModel(), LoginViewModelInputs, LoginViewModelOutputs {

    val inputs: LoginViewModelInputs = this
    val outputs: LoginViewModelOutputs = this

}