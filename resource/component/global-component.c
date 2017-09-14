<template id="euap-message">
   <span> 这是一个全局的自定义Vue组件</span>
</template>
<script>
    Vue.component("euap-message",{
        data:function(){
            return {
                message:'这是一个全局的自定义Vue组件'
            }
        },
        template:'#euap-message'
    })
</script>
