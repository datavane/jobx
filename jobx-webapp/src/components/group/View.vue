<template>

  <section class="content">
    <header class="content__title">
      <h1>DATA TABLES</h1>
    </header>
    <div class="card">
      <div class="card-body">
        <h4 class="card-title">Basic example</h4>
        <div class="table-responsive">
          <table id="data-table" class="table">
            <thead>
            <tr>
              <th>Name</th>
              <th>Position</th>
              <th>Office</th>
              <th>Age</th>
              <th>Start date</th>
              <th>Salary</th>
            </tr>
            </thead>
            <tbody>
            <tr>
              <td>Tiger Nixon</td>
              <td>System Architect</td>
              <td>Edinburgh</td>
              <td>61</td>
              <td>2011/04/25</td>
              <td>$320,800</td>
            </tr>
            <tr>
              <td>Garrett Winters</td>
              <td>Accountant</td>
              <td>Tokyo</td>
              <td>63</td>
              <td>2011/07/25</td>
              <td>$170,750</td>
            </tr>
            <tr>
              <td>Donna Snider</td>
              <td>Customer Support</td>
              <td>New York</td>
              <td>27</td>
              <td>2011/01/25</td>
              <td>$112,000</td>
            </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>

  </section>
</template>
<script type="application/ecmascript">
  import 'datatables'
  export default {
    mounted() {
      this.$dataTable()
    },
    methods:{
    }
  }
</script>
<style lang="scss" scoped>

</style>
