<template>
   <select class="select2">
        <slot></slot>
  </select>
</template>

<script>
    export default {
        data() {
           return {
               sel:null
           }     
        },
        props: ['options','selected','value'],
        mounted() {
            var vm = this
            this.sel = $(this.$el)
            .select2({data:this.options})
            .on('change', function () {
                vm.$emit('input', this.value)
            })
        },
        watch: {
            options(val,old){
                //this.sel.select2({ data:val})
            },
            selected(val,old) {
                if(this.sel) {
                    this.sel.val(this.selected).trigger('change')
                }
            },
            value: function (value) {
            // update value
            $(this.$el)
                .val(value)
                .trigger('change')
            },
            options: function (options) {
              $(this.$el).empty().select2({ data: options })
            }
        },
        destroyed: function () {
            $(this.$el).off().select2('destroy')
        }
    }
</script>


