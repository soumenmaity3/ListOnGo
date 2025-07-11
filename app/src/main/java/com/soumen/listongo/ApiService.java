package com.soumen.listongo;

import com.soumen.listongo.ForAdmin.AdminProductModel;
import com.soumen.listongo.ForCart.ForAllListModel;
import com.soumen.listongo.Fragment.AllListF.AllListFragmentModel;
import com.soumen.listongo.Fragment.ItemLi.ProductListModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

public interface ApiService {

    @POST("user/signup")
    Call<ResponseBody> signUpUser(@Body SignUpUserModel signUpUserModel);

    @POST("user/login")
    Call<ResponseBody> loginUser(@Body LogInUserModel logInUser);

    @GET("product/get-all-product")
    Call<List<ProductListModel>> getAllProducts();

    @Multipart
    @POST("product/add-product")
    Call<ResponseBody> addProduct(
            @Part("productModel") RequestBody productModelJson,
            @Part MultipartBody.Part image,
            @Query("userId") Long userId
    );

    @GET("product/get-filter-product")
    Call<List<ProductListModel>> getProductByCategory(
            @Query("category") String category
    );

    @GET("product/get-filter-product")
    Call<List<ProductListModel>> getProductByTitle(
            @Query("title") String title);

    @GET("product/for-admin")
    Call<List<AdminProductModel>> AdminList();

    @PUT("product/make-for-user")
    Call<ResponseBody> makeUserProduct(
            @Query("imaId") Long id,
            @Query("adminId") Long adminId
    );

    @GET("user/isAdmin")
    Call<ResponseBody> isAdmin(
            @Query("email") String email
    );

    @GET("user/user-name")
    Call<ResponseBody> fetchUsername(
            @Query("id") Long id
    );

    //OTP
    @POST("user/send-otp")
    Call<ResponseBody> sendAndStoreOTP(
            @Query("otp") String otp,
            @Query("email") String email
    );

    @PUT("user/use-otp")
    Call<ResponseBody> useOTP(
            @Query("email") String email
    );

    @GET("user/check-otp")
    Call<ResponseBody> checkOtp(
            @Query("email") String email,
            @Query("otp") String otp
    );

    @PUT("user/reset-pass")
    Call<ResponseBody> restPass(
            @Query("email") String email,
            @Query("password") String password
    );

    @DELETE("user/delete-account")
    Call<ResponseBody> deleteAccount(
            @Query("userId") Long userId,
            @Query("email") String email,
            @Query("password") String password
    );

    @GET("product/approve/{id}")
    Call<List<AdminProductModel>> approveProduct(@Path("id") Long userId);

    @GET("product/get-filter-product")
    Call<List<ProductListModel>> getProductByNickname(
            @Query("nickname") String nickname);

    @GET("product/is-exist/{title}")
    Call<ResponseBody> isPresent(
            @Path("title") String title
    );

    @POST("list/create-list")
    Call<ResponseBody> addList(
            @Body List<ForAllListModel> allListModelList,
            @Query("userId") Long userId
    );

    @GET("list/grouped-by-time")
    Call<List<AllListFragmentModel>> getGroupedList(@Query("userId") Long userId);

    @DELETE("list/clear-list")
    Call<ResponseBody> clearList(@Query("userId") Long userId);

    @PUT("product/update")
    Call<ResponseBody> update(@Query("price") double price,
                              @Query("description") String description,
                              @Query("id") Long id,
                              @Query("nickName") String nickName);
    @DELETE("product/delete")
    Call<ResponseBody> deleteProduct(
            @Query("proId") Long proId
    );
}
