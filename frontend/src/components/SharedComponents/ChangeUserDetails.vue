<template>
  <div style="margin-top: 2%; background-color: white !important; width: fit-content; padding: 50px" class="container">
    <h2>Change user details</h2>
    <div class="row">
      <div style="flex: 100%; max-width: 100%" class="col-sm-4">
        <label>Username</label>
        <div class="form-group pass_show">
          <input v-model="newUserName" class="form-control" placeholder="Username">
        </div>
        <label>Name</label>
        <div class="form-group pass_show">
          <input v-model="name" class="form-control" placeholder="Username">
        </div>
        <label>Surname</label>
        <div class="form-group pass_show">
          <input v-model="surname" class="form-control" placeholder="Username">
        </div>
        <button @click="changeDetails" style="float:left;" type="button" class="btn btn-info">Change details</button>
        <router-link style="float:right;" tag="button" to="/Profile" type="button" class="btn btn-dark">Cancel</router-link>
      </div>
    </div>

    <md-snackbar :md-position="position" :md-duration="duration" :md-active.sync="showSnackbar1" md-persistent>
    <span>All fields are required!</span>
    <md-button class="md-primary" @click="showSnackbar1 = false">Retry</md-button>
    </md-snackbar>
    <md-snackbar :md-position="position" :md-duration="duration" :md-active.sync="showSnackbar2" md-persistent>
      <span>There is already a user with username: {{newUserName}}!</span>
      <md-button class="md-primary" @click="showSnackbar2 = false">Retry</md-button>
    </md-snackbar>
    <md-snackbar :md-position="position" :md-duration="duration" :md-active.sync="showSnackbar3" md-persistent>
      <span>Changes saved successfully!</span>
      <md-button class="md-primary" @click="showSnackbar3 = false">Close</md-button>
    </md-snackbar>

  </div>
</template>

<script>

import http from '../../http-common'
import Vue from "vue"
import { MdSnackbar, MdButton } from "vue-material/dist/components"
Vue.use(MdSnackbar)
Vue.use(MdButton)

export default {
  name: "ChangeUserDetails",
  data() {
    return {
      user: null,
      previousUserName: '',
      newUserName: '',
      name: '',
      surname: '',
      password: '',
      showSnackbar1: false,
      showSnackbar2: false,
      showSnackbar3: false,
      position: 'center',
      duration: 3000
    }
  },
  methods: {
    changeDetails() {
      if(this.newUserName === '' || this.name === '' || this.surname === '') {
        this.showSnackbar1 = true;
        return;
      } else {
        http.put('/User/changeUser',
            JSON.stringify({
              previousUserName: this.previousUserName,
              newUserName: this.newUserName,
              password: this.password,
              name: this.name,
              surname: this.surname
        }))
        .then(response => {
          if(response.data) {
            this.showSnackbar3 = true;

            let changeUser = JSON.parse(localStorage.getItem("loggedUser"))
            changeUser.userName = this.newUserName;
            changeUser.name = this.name;
            changeUser.surname = this.surname;
            localStorage.setItem("loggedUser", JSON.stringify(changeUser));

          } else {
            this.showSnackbar2 = true;
          }
        })
      }
    }
  },
  mounted() {
    if (localStorage.getItem("loggedUserRole") !== null) {
      this.user = JSON.parse(localStorage.getItem("loggedUser"))
      this.previousUserName = this.user.userName;
      this.newUserName = this.user.userName;
      this.name = this.user.name;
      this.surname = this.user.surname;
      this.password = this.user.password;
    }
  }
}
</script>

<style scoped>
  .pass_show{position: relative}

  .pass_show .form-group {

    position: absolute;

    top: 50%;

    right: 10px;

    z-index: 1;

    color: #f36c01;

    margin-top: -10px;

    cursor: pointer;

    transition: .3s ease all;

  }

  .pass_show . :hover{color: #333333;}
</style>