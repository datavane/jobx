<template>
    <div class="pager-search">
        <div class="dataTables_length" id="data-table_length">
            <label>
                <select name="data-table_length" aria-controls="data-table" class="" v-model="search.pageSize">
                    <option value="15">15 Rows</option>
                    <option value="30">30 Rows</option><option value="45">45 Rows</option>
                    <option value="-1">Everything</option>
                </select>
            </label>
        </div>
        <div id="data-table_filter" class="dataTables_filter">
            <label>Search:<input type="search" class="" v-model="search.word" placeholder="Search" aria-controls="data-table"></label>
        </div>
    </div>
</template>

<script>
    export default {
        props:["search"]
    }
</script>

<style scoped>

</style>
