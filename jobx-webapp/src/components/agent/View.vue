<template>
    <section class="content">
        <header class="content__title">
            <h1>DATA TABLES</h1>
        </header>
        <div class="card">
            <div class="card-body">
                <h4 class="card-title">Basic example</h4>
                <dataTable :url="url" :column="column"></dataTable>
            </div>
        </div>
    </section>
</template>
<script type="text/ecmascript-6">
    import dataTable from "@/components/common/DataTable"

    export default {
        components: {
            dataTable
        },
        data() {
            return {
                url: "/agent/view.do",
                column: [
                    {header: 'Host', data: 'host'},
                    {header: 'Name', data: 'name'},
                    {header: 'Port', data: 'port'},
                    {header: 'Status', data: 'status'},
                    {header: 'Warning', data: 'warning'},
                    {header: 'Proxy', data: 'proxy'}
                ]
            }
        }

    }
</script>
<style lang="scss" scoped>
</style>
