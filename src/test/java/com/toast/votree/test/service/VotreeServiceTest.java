package com.toast.votree.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.toast.votree.config.ToastCloud;
import com.toast.votree.manager.DbShardingManager;
import com.toast.votree.service.DatabaseMapperRedisService;
import com.toast.votree.service.ToastSearchVotree;
import com.toast.votree.service.HitCountWriteBackService;
import com.toast.votree.service.VotreeServiceImpl;
import com.toast.votree.sharding.UserMapper;
import com.toast.votree.sharding.VoteItemMapper;
import com.toast.votree.sharding.VoteMapper;
import com.toast.votree.sharding.VoteResultMapper;
import com.toast.votree.sharding.VotreeMapper;
import com.toast.votree.util.RestResponse;
import com.toast.votree.util.RestResponseHeader;
import com.toast.votree.util.VaildAccessTokenUtil;
import com.toast.votree.vo.User;
import com.toast.votree.vo.Vote;
import com.toast.votree.vo.VoteBox;
import com.toast.votree.vo.VoteItem;
import com.toast.votree.vo.Votree;

@RunWith(PowerMockRunner.class)
@ContextConfiguration(locations = { "classpath:spring/applicationContext-test.xml",
    "classpath:spring/servlet-context.xml" })
@PrepareForTest({VaildAccessTokenUtil.class, UriComponentsBuilder.class, Collections.class, VotreeServiceImpl.class})
public class VotreeServiceTest {
  @Mock
  DatabaseMapperRedisService databaseMapperRedisService;
  @Mock
  DbShardingManager dbShardingManager;
  @Mock
  ToastSearchVotree toastSearchVotree;
  @Mock
  ToastCloud toastCloud;
  @Mock
  HitCountWriteBackService hitCountWriteBackService;
  @InjectMocks
  VotreeServiceImpl votreeServiceImpl;

  private MockHttpServletRequest request;

  Map<String, Object> Votree;
  List<Votree> votrees = null;
  List<Votree> runningVotrees;
  List<Votree> expiredVotrees;
  Date d = new Date();
  User runningUser;
  User expiredUser;
  String userId;

  /* searchVotree */
  String votreeName;
  int offset;

  /* base64 */
  String base64;
  String base64Image;
  byte[] imageBytes;
  byte[] byteMock = { 1, 2, 5, 8, 1, 3, 6, 0, 1, 2 };

  VotreeServiceImpl spy;

  private List<Votree> setVotrees;
  VotreeMapper votreeMapper;
  VoteItemMapper voteItemMapper;
  VoteMapper voteMapper;
  UserMapper userMapper;
  VoteResultMapper voteResultMapper;
  List<Votree> votreesDb1;
  Votree votree;
  User user;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() throws JsonParseException, JsonMappingException, IOException {
    spy = PowerMockito.spy(votreeServiceImpl);
    MockitoAnnotations.initMocks(this);
    votreeMapper = mock(VotreeMapper.class);
    voteItemMapper = mock(VoteItemMapper.class);
    voteMapper = mock(VoteMapper.class);
    userMapper = mock(UserMapper.class);
    votreesDb1 = mock(ArrayList.class);
    voteResultMapper = mock(VoteResultMapper.class);
    votree = mock(Votree.class);
    user = mock(User.class);
    setVotrees = new ArrayList<>();
    runningVotrees = new ArrayList<>();
    expiredVotrees = new ArrayList<>();
    userId = "12";

    JsonBuilderFactory factory = Json.createBuilderFactory(null);
    JsonObject value = factory.createObjectBuilder().add("startDatetime", "2016-02-17 00:00:00.0").add("hit", 22)
        .add("dueDatetime", "2016-02-19 00:00:00.0").add("turnout", 1)
        .add("subVoteList",
            Json.createObjectBuilder().add("duplicateYN", "N").add("previewYN", "N")
                .add("voteItemList",
                    Json.createObjectBuilder().add("itemId", 244).add("isVoted", 0)
                        .add("value", "d831eacb44fa4cf0be3393f6ca204d88.png").add("categoryId", 2))
            .add("topic", "이미지테스트성원지우지마").add("weight", 0).add("voteId", 164))
        .add("proposerId", 8).add("id", 103).add("title", "이미지테스트성원지우지마").add("type", 1).build();
    Votree = new ObjectMapper().readValue(value.toString(), HashMap.class);

    runningVotrees.add(new Votree().setId("1").setProposerId(1).setType(1).setTurnout(1).setHit(1).setTitle("저녁")
        .setStartDatetime(d).setDueDatetime(d));

    expiredVotrees.add(new Votree().setId("2").setProposerId(2).setType(2).setTurnout(2).setHit(2).setTitle("아침")
        .setStartDatetime(d).setDueDatetime(d));

    setVotrees.add(new Votree().setId("10").setProposerId(10).setType(2).setTurnout(10).setHit(10).setTitle("테스트")
        .setStartDatetime(d).setDueDatetime(d));

    runningUser = new User().setId(1).setEmail("running@email.com").setName("이동주").setOauthProvider("Facebook")
        .setProviderKey("DKF299DKFJ").setCreatedAt(new Date()).setUpdatedAt(new Date());

    expiredUser = new User().setId(2).setEmail("expired@email.com").setName("오준영").setOauthProvider("PAYCO")
        .setProviderKey("DSK99212SDF").setCreatedAt(new Date()).setUpdatedAt(new Date());

    request = new MockHttpServletRequest();
    request.setParameter("userId", userId);

    /* searchVotreeTest */
    votreeName = "테스트";
    offset = 0;

    /* base64 Test */
    base64 = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcUFhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSgBBwcHCggKEwoKEygaFhooKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKP/AABEIAMgAlgMBEQACEQEDEQH/xAGiAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgsQAAIBAwMCBAMFBQQEAAABfQECAwAEEQUSITFBBhNRYQcicRQygZGhCCNCscEVUtHwJDNicoIJChYXGBkaJSYnKCkqNDU2Nzg5OkNERUZHSElKU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6g4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2drh4uPk5ebn6Onq8fLz9PX29/j5+gEAAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoLEQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APqS4lSCF5ZOFUZNKT5VcaXM7HnetOt7qjO0wUOSAT0HoK5W7u51pcqsYuvW7/Y5mlC527Rg5BA75poV7o81jkI1AZHKEq35HH8jW19DBKzMd71orlkBJ+csB6+1aIGzW8+OW13RkDGD6damwMozyBXByCD+tNEbEcMgdWGeQcihj3IJn/iU49/SmIrR6hyUk4K+tVYC0t2wI/iX2pWKvYnSdJACpIzx7VI9y3aXYjlAmO3sH9qg0g+Vnovwiu3h8YvBkeXcwseO7DnP6UX95CnHRs9wrQwCgAoAKAPKv2o/+SE+Jv8At1/9KoqAOy8aX32XT0UOFMjc59BzWFZ9DagtbnBaasuqWruh2XAlLqc5yAcECsorobVO5Fr97EiMAhiSTCsueFb5s8dv/riraM4ux5tfqItXl5IHlD6EnjP5/wA6tA0c1fYaZXBwd2c9P8//AF62RiyVbsL0Pysvzjse2fzBH40DbIpZS0I55Q5Hv7UWIvchjutsiOG+Vj/n9aLDTsRm6B8yNiR2/GqsIzZZt5zzu9c0xktrduoAOcdMGgErluC4KyckgHr7j/PepsWtDVt7hXBikOePpkf41nIta6np3weAfxJYAk7oxKA3qNhrO/voub/ds98rc5QoAKACgDyr9qP/AJIT4m/7df8A0qioA0PijHLNhIs5EXyjpznOP0rmm/fOml8BmeC4hZMYpoGfaPOiZujqRg7T6g9u9JBJ6mB4sMc8yuhJikBwRxz2B9xVCW5xGsyhhZbR87Jsb6qTgfqDTiORy13kRYzyMgD36/1FbXMGii8mZAeMA4/4D/nNMEyE3Qjl2MTtcfKTVJEleOYgujckPkfj/n9aBFe7ndXSVDns31//AFYpiGu4OJASFbnHpQWhZHIHGc+3p/ntSQLQntLzGBIcj+VJl3ND7QI2BRsj+h7VLVxxdj1H4Y6kkPinRX+YJPMIztHdlYKfoScH/wCtXP8AaN56wPpSuk4woAKACgDyr9qP/khPib/t1/8ASqKgDs9et45r5DIFOUwoPTII/oSPxrkqfGdNN+4cvc7YAsEwAe1LDcBzsP8AT/69KLE1qcb4oV47p4kYm3ky6nqUkHUfnz9DVJlJHmmt3Z3jsd2evcnP8gK1gjOo7GTfSf6SjqSRIAV/LFaWMrmTPMwnznC9fYd/5UxGWbonMTffQnHPcVQDjOQRLnPQMM9xQ2KxIXBJGRtPr+lK5VgRSAyOcr1BPapuUkKpbOG5A7H+lLmKJ/KV/mVj7+op3ARFlE/lPkq3Q56elFwS1PcfgRobXOtq90CY7ONbj235wmD/AN9H8D61zw96ZvW92B9E10nGFADWYKMscDIHPqeBQA6gDyr9qP8A5IT4m/7df/SqKgDvfEMIa0dwoZkR2APQ/KeP8+lctRWaNqTOM1SZWkFwoDJgK5Pv396hLQ06nH6q/mfaVkJ3g5UfTgfXA/SmWeSeJYDE1zsYEg+Yv4DHX6fyrpps56hjRXQuLSEZxJG3y+uehH6frWrMUV5AIpMP0zt/XI/pU2GZF5GUuSw5B5z+tFxpCNyu44KsMN7Glcqw6JzGwRySD0NJsaRaRSPlGTjpzU3KsWY8OAGIB9elT6MpXe5N9ml5MYyw/wDHhRzdy1B9DW8PW4vG8q4gKuh+QkcfTNZzn1NKcU/iPpXw7JL4a0OwSKBdhQNMRn7x5rznWdOXMjvWGjiLwvtseg2Nyl3axzxHKOMivWp1OeHN3PEq0pUpuEt0T1qQIyh1KsAVIwQaLANjJHyNyR0PqKLAeXftR/8AJCfE3/br/wClUVAHpl4gdQGGQeCPUGuevui4M8r8UWc2nTHy1LoVK9cblbqPbB5H1NZw2N2ctdRu9oyO37+PmOXHJGRgN6+n5VfKJSOG8YQ/aY0nhQLIPvD0I6j6f4+1XF2Jaueb3CtDLIi8Bz5if1H4GtzCw6a4NyqMxI4Ab2PrQKw8ESxHeRuXgj1qGaIpkorsin5T254NJDYqxnbgA89OM1LZSiXbS3uJ22RRtI56Kq5NQ2axhc6K18Ja7MULaXdENxkIf6dPxrH6xSj1N1hakt0dz4X+Guqpdxm+RPshXO7OGU44GP8A9dYzxHN8JvCgofEy8dITRNbMEgGCRyRwRmo5ueJSh1ex7BLfRQq8MsWY9oBJ5DcCudzWzLhQlKzi9TovDojTTlji+4pyufQ8ivTwekOXseZjHKVRyluzH8RfEDw5oN39ju78TX2cG3tl8x1Po2OFPsxFdhxnP3Pxn8NWsqpdQanEWGRmFGz/AN8uaVxcxG3xn0GYD7Fpus3J6gpDGo/NnFFw5jzz4+/EUa98Kdc06PSJbZJvI/eTTqWXbPG33VznpjrTHe59JSLuUioqRuik7M4/xLaCdQh+WYZKsRkYrlS5WdKfMjy3xSXsirFDG2RwPut6j8fyro3MUrHCa1I2JHtyHywYdwRyM49weR70noarU5DVLeK4hDxghs/c7/gfWmpW6kuLZjmJ4zu4ZW4I9avmv1IcGg8tuHj5B4xip5ooapvoTJp88wDrBKfohNTzpdTRUZyPQvhr4V1W9vELWxW23As0keRjPvjNcWIqrZanpYei4e9Nn0Bpnh2wtcPHaQpIP4lQA/nXDa+rHKu9lsa620S42oo+goRi6jJfLG3tWiZPMcR490T7QBdRD94o5x3pp21Oik21ZGpdWc9/a6abZ41F1EGfd/CcDJHr1rOcb6G2GxMKXM6i+ExPij4guPDHhi1trSZo5L25W1LjIYqI3Y4I6ZIUfQmvRwXU8rGPmtI+YrXUn/tBwxwQeO3evRsef5lvVb37RJA0h4BxmkI1RrohiCR4VOgAoAxPGustceD9RiZtxlMajPoHU/0oYR3PvikaFPUrNLyHBGHXlSOorOcLlQlY898UeG7i8hzhJrcja3BweeMehyOR2NZLQ0crnFW3w1vLq4BltruKFsjJx8oPBHrg/wD16TuzSMkjfsvgrpM0TC9urx+cgcKfzA+lZXm+pcq0FshLz4JaAP8AlvelT1BkU/0rKVScepcKkJdBbP4UeHrNsok0hzwXfJ/wP5VjKtUb1Z0RlCK0idhpvg7QdLtkEdpAjk4B2jJJ/Un6VpyJrUwliql/dOHl+I2nR6qtjo2mTywLKsctzL+5RAWClguCTg+oXmpdB2uzbncnqdNpmpardyS+fbwxQK5EbqTllB4OD0z1/GuaUtbI6XShHXqbtu7EDccmkmc80i4oyK0Rg2V7mBJVw6gj0NO19DSEmhIkRFjAAHlghfYd8flQn1E7nz7+1Hqhi1Xw5bRscQLJcsAeeWAH/oDfnXo4NbnJiXa0Twy6l8rUC6kYDZHpiu5nJa2hZuLjK7Sc5oM7ka3ZIAJPpQMz9duGk0eRCehH/oQoew47n6TVJoFADEjVCxVQNx3HHc0rAErbUJ9Kzqyshoz11NPM2upFcUcTrsdP1WW6ZcYLPFlSCCODXRKCrQ0OdOUJGFdgxykdDXlSTTselTtJFKSHUJfEFhfRXVulnBEyPFIpJZmPJBHTgD9a3pVLSUhS9mqcqbW5TufDWiPrUmqfYoftjt5h2ZCl+7bc43d84689aKtTmCkuVGoIWbAVdqjpxXOoXNOdEyQhOtUokudyZeBzxTM2Mc80dGNLYx/Empf2Tol/fiPzfssDzeXu27toJxnnGcdaUVdpGuybPivxn4qvvF2vf2hqrKJpk2iNPuxKCdqD2GfxJJ717lKmqSsjyalT2srmDdMRCjNksAAfX0rRb3M29dB/n+ZGGzkigRGZeSQR6Gi4ipqcgaylA9v5ih7DitT9Nqk0CgAoAaVyQSTj0qXFSC55b4g8ALD4gl1Gz1OeCO4l89gAfMVt24hXzwpPGCOBxzXDiHyaWPQwr5t2ekaYCLRCc8889fxrbDJqF2clb4yrqsWZAw9K48VFRmzfDTsU4124BrnWiOiT6l2CMN0A/KtVZnPOViSSMKpwOabViYybKbZJ5qGbpC9RUgROOlS3oVFnD/F27Fl8O/EMzHpZuo+rfKP1Iq6GtVIuf8Js+I75v3xlQ4DncMdj3r3VfqeP0BLkSgpIOWHNNu4vhV2RxuY5DGTnHP1oEShsD1pAkVL9v9HcH8Ppmmykfp/UlBQAUAFAEbxoxBZQSOhIrKVKMtR8zQ/irVlZC8yveLuSuLFrW5rTdmYtySnTNee1qehCz3IbHVVFybacbJAMrnow9RTjU7hVwztzQNfzQ68c1s5XWhycrRA3UVmzTRjH6UithjD5al6opas8c/aW1D7P8PZrND+8vZ44sf7KneT9Mqo/GtsIrVkx1rqi0j5DVy0boxyVOQK9qx5DKoYqwYUxF5x50QZf9YvI9/akOw1HDrkde4Paiw7EF6SYWDDp0psE9T9Q6ksKACgAoARjxWdRtRuBQuZGaWNFbblhmuFy55pXN4xsrlucfuzXTiVpcyhozKnkiXl2UfWvLex2xTexg6iIbq7geFwXjfcSPTBBFYz12O+jzQjys07a5EKgNytXGfLuc1Sk5bFkTpKfkPPpVJmXs3HceB1zTFcjlYKhJpPYcVdnhXx1hGpx2gYkxxs8ZH+8Bz+lFCdm2d3IrJM+Ub1WtL+RSDlGKN9QcGveTPAaIJQFJxyOoqmSWLByyFSRxSuA6VNjF1/4EP60DIbtla2Yj2/nQxn6hA5ANIYtABQAUAIxAHNTJXVguZOpb45FYfhXlVk6budlGzVmaSfvYlLchhk4rviueClI5L2loZOpWMZnjJUmNztI9DXFiaPJNWWh20K75Gr6mdJYxWrNudVweSTXG6djqVeVToZmua5pmiWbz3kpIRDIFXqwHpnAP50KJrTo1arajoQeDfEUXiKw/tCKzntYjIVjEpGXXA+bjoDmi1mPEUJU/cZ10rKACMYNaPVHnRu7p9DI1a88qBsHnoKynLQ7KVPU8g+JMge0hX/pqD+hoovU6Z6I+W/Fig69dBcEeYen6/rXvUdKSR4WId6rZkg7oAc8pxWzMBLNykwHY0mBpjkAjBpDuUL1CitgfK3X2qmM/UToT6VIC0AFAEVzPFawST3EixwxgszscAAd6APnH4mfE/UdenltPDty9hpMZIEy8S3DD+LP8K+mOT19MNRM3O5v/Br4pf2q0XhnxfOo1UEJZ3ch/wCPkdkY/wB/0P8AF/vfewrUVM1hNo9ogZoiY36fwmuajUdL3JGk1zrmRz+q61qtveRxtot19ibO6eJkkZfT5c//AKs1VV1JataHZSw9Bw5ozvLtsZ0+pRKZGsbK4lu+onuug/D8+wrknE7qWHlKyqytHsjjr/QJtcu0bWZPtaLIXjt9vyKSenqR9awaserGdKjH3Pmd3plgljaxwKoG3kgVnLVnkVqzqzcy/K4RMk8UNtI5oJylZHHa1qAd2O8BF4z2rPc9GnTseO/EvXoo4Qd3yRZbr1/+vXZhqdzDEz5T51vJ2ub5pWOXdmdvqTmvbjG1keDKV22QW2N0iHoRT7i7EI+VweetAjVQ5jH0pAQ33Ns/4fzp3Gfp/SGFABQI+d/jz4/+1XMugabKRaxHFy6niRv7v0Hf8vWqSMpS5nyniMd8zPh3yOh71QrkeqRfaoMjBlT7p9R6f4UhpnpHw3+Pmo6IsOn+K4pNW09MKtyDm5jHuTxIB74bryelYTo87uaxnfc+nfDuvaZ4l0iLUtEvI7uyl4EidQe6sDypHcEAihN7DZX1HTC0haMsQe3FcNZSR6FDEpaSK1vYC3yQgUnqTXG4uW5tUxHOTrEqck5as3psZ87ex5D8X/inYeGdRTQ4GSS/KCSfL4ESn7oOO5HOPTHrWtLDTq6x2NadWFLWW54/qnxSimQ7p92BwkYNdEMFJvU1nj6dtDzHxP4ln1qYM+UgH3Uzn8T616FKkqaPKrV3VZg2/V5G69BWxgJbf8fIzxQA24XbK4xgZoEXrNy0OTj/ABoEJe/8er/h/OlYo/T+gBO9AHHfFPxL/wAI34WnkgcC/uQYbcHsxHLfQDJ/CmkZznbQ+ONQmaaZ3ZmZiSSzHJJJyST6mtDJMzidp4oBli0uFdvKkPB4z6UgMfVoDFck8Ak849aGPfY6X4Y/EPU/AGufbLH99ZzYF3ZO2EmUdDnsw7NjjocgkVDszWLtufXfhT4o+EfFNms2n6vbwzkZe0u3EMyH0Kk8/VSR71x13Y2hFy1iXdY8S6RYW5uL7VLG2g7PLcIg/MmvPleWx2wp20PDviP+0Np9lHJZeDVF/dnKm9kUiGP3VTgufyH16VvSwcpu89ETOtCK93VnzBqFxPqV9Pe3t3LPdXDmSWWQ5ZmPJJNepGKSstDglJyd9yD7MSeH4xVNtit1E8hIxlzmhW6g2paIaSXPA2qOcUihicXCnAAzQA+84l6DpQK4/T2+Ypn3oFYm1AYgf8P50ij9PqAMPxTq/wDZlliDa13IQka5yQTnBx+B9uD6UESlynzb8Q/EMmsa/GTO0tvBuiiYtkv/AHn/ABPA9gPWtEczkedzqVmkTaBg0AjPuo8lhjnHFBSZls5QnnFA7Et6/wBpsopSAWGUb37j9P5UDRiSAo2D07UmO5C56ggEUrDKV1AOWVQMegp2GV8EjuT3pFXALzmgdx0chXAbOKQMnERnYCMF2PQLzn6Ck3YErF6Pw/rE9s91BpN/JaoDumS2couOuWAwMVHtYJ2uri9pC9roy5LeSGdRLG6E8gMuMirTT2K32C+UBgcDNAWIrVis4I78UxF7UAPsbn6fzpFXP07oDY8h+ImuWOpJJLC2YoZTbiXeRlgOSuOuckA1cTmqO54pqHkmdtlsyhTnzMlh/wDWpkGNfqgfzFIPY4oKMa/YHD8ehoEYt2Bneo+U9cUFIjt33RzxE8HDj8OP6/pSYyrNHvBHcdKVxpFEqeQ3WmNETDbkN0NK4yOSInle9K4yMR4GDRcZ6X4F+Fk+rRw6j4gZ7TTXAeOFD+9mBBwf9kdOoyR6cGvLxeZRpXjT1Zw4rHxpe7DVnr2i6Ho2hQRRaRpltAY+krIGlPJOS55PU9+OgwK8Kri6tV+8zxquKqVH7zNX7VN/f/QVhzsw52UtUsbDWIjFq9ha3iFSv72MFlH+yeoPPUVrTxNSm7xdjWniJ03eLPLvHvwmWeF7zwkcFFy1jIxLHGc7GJOewwfTr2r2cLmil7tX7z1sNmSl7tX7zxTY8cu1lKurYIIwQc9K9lO56u+xoXw3afIfTH8xQOx+inxF1ZtG8H6hdRsVlKeWhHYtxn8s00RN2R89ag6HTNPE4EtnLEAwbkJJuJ3fXk1ojlkcldkzO6W6eTIuTGysSrY7EHkfhQJHOvfKxMbL5MynDKehNBRSuJDyCBg9qQ0zOZgcg9DxSLK5zHMrDoTtP40rjBz8xHT8aQ0ivcxjhxjmmFirIgIyB1pMpIYpAOGODSKSPSPhJ4Ri1W9bWNTiY2VowMUbp8k789z1CkA49cfj5mY4v2UeSO7ODH4j2UeSO7PaSXnl6FnbgAD+VfNayZ8/rJl+20suivKxVN218EfLyQTk8cehI+oyM7xoXV3/AF+n9ehtGjfVjZdP2yghnSDdhmK79gJODlevyjOeM4OOlS6Nn5f128tROlr5f1+mpFc2LwhGVg6OpcHBUhRnGc9CQpIHpiplScbeZMqTiVVJUgqcEVnsZnl/xn8FpfWT+ItJgIu4eb6ONSfMT/npj1Xv7ZJ6V7uWYxt+yn8j2MvxTb9lL5Hjt4P+JVIT6L/MV7rPaPvT45I8ngh0j5YTK5UdSADn+YoiZVDxCylSXRkQ4xjBB6cVbOdanPXS+TKDCQyg9M0XCxzniS2SRxLHw5GSO+KZSRgCYqNkhJXse4oHFFeX73se46VJZDJIOATwePxpWBIJG+XPODzSGC4dCCBn2oYyNkwcZzSNEiA2zzSLFBGzyuwVFUZZieAAO5pMD6b0DS49C8PafpkJJ8mPc5Pd25Y/mTXyGLqurUcmfLYqq6tRyZ0Wj2pnZ1WKYyhSwMa8gYBHqRnsQOpHXOKKFPm0tr/X9bCow5tLamg16WRZoLkefgs0EhZs4VTuUrwDtBBORgDgLgAb+1uuaL17P9LeWm68raW39pdc0Xr2/wCG+7f7iSWNnsrjbPNFaQwxOWJy205Cx8ZIyGkx0ByM4BFNxvB6tRSX/AXXu/LbuNq8XrZJL/hvxZPqPlS3DC6d0SF3g2hisasq4YJ94kA4HTgFTg/NnSraUvf6XXl523enp203LqWb97pdeXnbf8u2m5h63HZBkksXj2MAoRZGY8DBJyi9ev59q4sRGnvT/X/JHJXUN4f1+CM6JVk3QygmKVTG4BxkHjtWVOTjJNGVOTjJNHzB4s03+yW1WxJLfZpjECe4D4B/EYr7KnU9pFS7n1tOXPBT7n2z8VpES8sj56h1if8Adsf4SeT+OP0rZIzqs8Q1sQNHKjSBHY8GLj8+1UY7nEX1pNHkwTGT6Hn8qYzFuJplciTLf0oKKcmHBeMZPdaRRWLq4Ixj0zUtDZRuSUU845oRTHI++HPpQxWFgOCetJjRYYbxjrUFM0fCQ/4q7Q89RfQf+jFrHE/wpej/ACIr/wAKXo/yPpC8/wCPhs+1fIT3PlJ7m0Fe4QtA024qFO+QykHPCtjBXjjcAclsEjJA67OSvH/P/gr18+my67OS0/z/AOG9fPp0nsofst1KnzQOMBYm+YxsefkkI/dk84JHT1xmtKceSTW3l29H09SoR5JNbf10fQ3tIRby1jkKRTh7oPLbR5BQrDJgED7vKJjtnIHGK7KCVSKe93qvRP7tl8/I7KKU4p766r0T/wAkZEyu0sgvGxdzOZGJfzGyOkYAG1j8wJHC/McjgAc0k7vn3fz+XZ73fTXY5pJ3fNu/n8uz/LXYo3rxmz4iMtw6kySNsZAGPy7ArYQ5AGAM4B7DFYVGuXa767W12tZ6GU2uXa7+Vte2uhhBWSYKwIYHkVx2s7M47WZ4P8aoFh8Q67sQIGML4AxyVjJP4k5/GvrMC26Ebn1ODbdGNz6G8ZFL++nu538ydzxk8IOwH0Fd4panmmrWpLttzg9PSmK1jm7m0kU53qoHUk0DsZN2ULbZCJMcZ7/nRcdjKubQZ3W7c+h6/nSuMouCT+8UpIPXvQxspX6h4WzxxSQ7leyJEXzChjJYT1wakC0pxjJpMot6ddyafqNrfwKjS20qTIHGVLKwIz7cVMkpKzG0pKzPpMTpeW1teRHdHcRLIpxjqM/1r4/EU3TqOLPlK8HCbiyS3uZreTzIJXjk5w6HDDPXB6is4zlB3i7MiM5Rd4vU1Ita/d7btJbwEgYuZS+0dypGCp4GOceoNdCxOlprm9X+XU3WI0tLX1Z3Pg0uYtSlZmcImI7xEH75MEqW9XUfj82DnAr2cDe03+PddPmv+HPVwV7Tf49/+Cv+HOFj1WO3E62xnG9mZnkwxnBx8r9CAeehPU9eteMq6jdRvr+Pr/w55SrqN+W/+fr/AMOQ3GoQ3DO/2ZbfcoBjgGEYg8ZBzg47j6YGSamVaMru1vTb+v68yJVYy1tb02KdupluV6nnPPNYxXNIziuZnz38Tp1vLrXblJPMRp8K4bcGUOACD6YHHtX2FCDhCMX0R9bRg4U1F9D3bVblZBl3GDyK6bmRx2s3oClYwST3ouBytyJJSdxJ56UXAqyWT8kqQPU8Ci4ylcKq8DANAWM6bDcMM/WgdyjJAHLBeQDyPShOysMnuNIa0tEkIOG6/UjNO1lclMoRoBJzzj1qCix5fGT3pDQ4KR16Uhpnsfwh1uO+0Z9DnZVubXMluSfvxk5I+oJ/JunBNeNmeG5l7WPzPLzLD3XtV8ztCCCQRgivBPECgR2/gPUbCy0nUor28igknO1VYHP3Tz06c/p9K9fLq1OnTmpytf8AyPVwFWnTpzU5WucXNH5Uzx70faxG5DlW9x7V5Mlyto8ySs7DKRJneLdVGg6BK6uo1C5HlQKeoz1b8Bz9cV6mXYXnlzy2R6mX4bnlzy2R4R4oh2+G7tsf3P8A0Na+iVr7n0LtbQ9lQPOoSRD5gGMGtDlM+7sI0Ba4cIo5x3ouFjGury3iyLeBfYsOTQNIxLy6lmY7jkUDsZk0ZZsmgZWlgyD60CsdX8JNMS9+ImipJbR3CCRjJHKgdSoQnJB444I9Dipm9LlWO5+OPg1NOtpdSsIcWUsgZgo4hcnp7KcnHp09MuEudWIseEtERIaY7k6RblwBzSGOMZB+apKJbG4n068hurSQxzwsHRh1BFJxvoyt1ZntnhPxfp/iSFYbp4rLVhwYmbCydOUJ65P8Oc+ma8PF5a/ipfd/W542Ky5r3qX3HRS2ssTEFCcegryJU5RdmjyZQlF2aIdp9DUWJsPjgkf7qH8aai3sNRbKmuazpvhqESajL5l2RmO1jI8xvQ4/hH+0fwBxXp4XLp1GpT0R6OFy+dRqUtEeWXl5qXirWkllDS3ErCOCCEFgoJ4RB+P1JyTya92MY04qMdj6CFOMIqMdj0P4lfDyHwn+z/4kvdQVJdamS2DMCCIFNzF8i+p9T+XHJcLt3LlJPS5mXH223B+zzJKBwM8NXSjlSMK/uLmRv9JVgT60gbMudQ3NMEVHUAHjpSKK7JnPNICB1WlcDvfgXDPJ8Q7aWEHyYYZWlPbaVKgH/gRFRUGmfQnibTI9Z8P3+nygkXETLxxz1H6gVnFhY+Qta0Sawv5baeJkdGIIIwa6UTcqwQlDhgcA0mFyWe2wMgcHpUsaZVaAgZ4x3oLuRvEc9PWgdzotJ8Z+ItMjigh1F5YIwFWOdRKAox8o3AkDgDgis6lKFTSauROnCp8audIPinq7Nk6XpA+iS/8AxysHgaDd+X8zH6lQbvymfqHj3xDevL5V0tkjdEtECbeMcNy//j1awoU6bvGKRtChTp6xiYNjYXN/eIkcctxcTPtVVBd3Y9PcmrlKxuon1N8JPhvD4Utk1DU0SXW5V9mW2BHKqe7Hu34DjJOfLeVwbKv7Uf8AyQrxN/26/wDpVFWsdyT/AP/Z";
    base64Image = "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";

    List<VotreeMapper> list = Arrays.asList(votreeMapper, votreeMapper);
    when(dbShardingManager.getMapper(anyObject(), eq(VotreeMapper.class))).thenReturn(votreeMapper);
    when(dbShardingManager.getMapperInCommonDb(eq(UserMapper.class))).thenReturn(userMapper);
    when(dbShardingManager.getMappers(VotreeMapper.class)).thenReturn(list);
    when(dbShardingManager.getMapper(anyString(), eq(VoteMapper.class))).thenReturn(voteMapper);
    when(dbShardingManager.getMapper(anyString(), eq(VoteItemMapper.class))).thenReturn(voteItemMapper);
    when(dbShardingManager.getMapper(anyString(), eq(VoteResultMapper.class))).thenReturn(voteResultMapper);
  }
  
//  @Test(expected=VotreeIsCompleteException.class)
//  public void getVotreeWithUserChoice_만기된투표일때_에러() {
//    Date expiredDate = Date.from(Timestamp.valueOf(LocalDateTime.of(2000, 1, 1, 0, 0)).toInstant());
//    Votree expiredVotree = new Votree().setId(UUID.randomUUID().toString()).setDueDatetime(expiredDate);
//    when(votreeMapper.selectVotreeByVotreeId(anyObject())).thenReturn(expiredVotree);
//    
//    votreeServiceImpl.getVotreeWithUserChoice("votreeId", 1);
//  }

  @SuppressWarnings("unchecked")
  @Test
  public void getVotreeWithUserChoice_성공() {
    Map<String, Object> map = new HashMap<>();
    Map<String, Object> subVoteMap = new HashMap<>();
    List<Map<String, Object>> subVotes = new ArrayList<>();
    subVotes.add(subVoteMap);
    
    Map<String, Object> voteItemMap = new HashMap<>();
    List<Map<String, Object>> voteItems = new ArrayList<>();
    voteItems.add(voteItemMap);
    
    map.put("id", UUID.randomUUID().toString());
    map.put("startDatetime", "2016-02-17 00:00:00.0");
    map.put("dueDatetime", "2016-02-19 00:00:00.0");
    map.put("subVoteList", subVotes);
    
    subVoteMap.put("voteId", UUID.randomUUID().hashCode());
    subVoteMap.put("voteItemList", voteItems);
    
    voteItemMap.put("itemId", UUID.randomUUID().hashCode());
    
    when(votreeMapper.selectVotreeWithUserChoiceByVotreeIdAndUserId(anyObject())).thenReturn(map);
    when(votreeMapper.selectVotreeByVotreeId(anyObject())).thenReturn(votree);
    when(votree.getDueDatetime()).thenReturn(Date.from(LocalDateTime.of(2020, 4, 17, 11, 10).atZone(ZoneId.systemDefault()).toInstant()));
    
    Map<String, Object> resultVotree = votreeServiceImpl.getVotreeWithUserChoice("votreeId", 1);
    assertThat(resultVotree.get("id"), is(map.get("id")));
    assertThat(resultVotree.get("startDatetime"), is(map.get("startDatetime")));
    assertThat(resultVotree.get("dueDatetime"), is(map.get("dueDatetime")));

    List<Map<String, Object>> votes =  (List<Map<String, Object>>) resultVotree.get("subVoteList");
    assertThat(votes.get(0).get("voteId"), is(subVoteMap.get("voteId")));

    List<Map<String, Object>> items = (List<Map<String, Object>>) votes.get(0).get("voteItemList");
    Map<String, Object> item = items.get(0);
    assertThat(item.get("itemId"), is(voteItemMap.get("itemId")));

  }

  @SuppressWarnings("unchecked")
  @Test
  public void sohwDetailVotree_성공() {
    Votree votree = new Votree();
    votree.setId("1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f");
    when(votreeMapper.selectVotreeByVotreeIdAndUserId(anyMap())).thenReturn(votree);
    votreeServiceImpl.showDetailVotree("1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f", 1, "detail");
  }

  @SuppressWarnings("unchecked")
  @Test
  public void sohwDetailVotree_SelectOne이NULL인경우() {
    Votree votree = new Votree();
    votree.setId("1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f");
    when(votreeMapper.selectVotreeByVotreeIdAndUserId(anyMap())).thenReturn(null);
    votree = votreeServiceImpl.showDetailVotree("1457062854241_7f0f6a8ab52644079dc6f1eec71dec1f", 1, "detail");
    assertNull(votree);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void modifyVotreeTest_성공() {
    String body = "{\"votreeTitle\":\"투표등록테스트!\"" + ",\"startDatetime\":\"2016-02-02 02:06\""
        + ",\"dueDatetime\":\"2016-03-16 03:00\"" + ",\"isPrivate\":false"
        + ",\"voteList\":[{\"voteName\":\"투표등록테스트!\",\"itemList\":[{\"category\":\"텍스트\",\"value\":\"투표등록테스트!\"}],\"isDuplicate\":false}]}";
    doNothing().when(votreeMapper).updateVotree(anyObject());
    doNothing().when(voteMapper).updateVote(anyObject());

    RestResponse resultResponse = votreeServiceImpl.modifyVotree("1", 1, body);

//    assertTrue(resultResponse.getHeader().getIsSuccessful());
//    assertThat(resultResponse.getHeader().getResultCode(), is(200));
//    assertThat(resultResponse.getHeader().getResultMessage(), is("투표 수정이 성공하였습니다."));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void modifyVotreeTest_실패() {
    String body = null;
    doNothing().when(votreeMapper).updateVotree(anyObject());
    RestResponse resultResponse = votreeServiceImpl.modifyVotree("1", 1, body);
    assertFalse(resultResponse.getHeader().getIsSuccessful());
    assertThat(resultResponse.getHeader().getResultCode(), is(400));
    assertThat(resultResponse.getHeader().getResultMessage(), is("투표 수정에 문제가 발생했습니다."));
  }

  @Test
  public void showVotreesTest_MainCall_성공() {
    Mockito.when(votreeMapper.selectVotreesOnRunningState(anyInt())).thenReturn(runningVotrees);
    Mockito.when(votreeMapper.selectVotreesOnExpiredState(anyInt())).thenReturn(expiredVotrees);
    Mockito.when(userMapper.selectUserById(runningVotrees.get(0).getProposerId())).thenReturn(runningUser);
    Mockito.when(userMapper.selectUserById(expiredVotrees.get(0).getProposerId())).thenReturn(expiredUser);

    Map<String, List<Votree>> votrees = votreeServiceImpl.showVotrees(0, 0, 0, 12, "mainCall", 1);

    assertThat(votrees.get("runningVotrees").get(0).getId(), is("1"));
    assertThat(votrees.get("runningVotrees").get(0).getTitle(), is("저녁"));
    assertThat(votrees.get("runningVotrees").get(0).getHit(), is(1));
    assertThat(votrees.get("runningVotrees").get(0).getType(), is(1));

    assertThat(votrees.get("expiredVotrees").get(0).getId(), is("2"));
    assertThat(votrees.get("expiredVotrees").get(0).getTitle(), is("아침"));
    assertThat(votrees.get("expiredVotrees").get(0).getHit(), is(2));
    assertThat(votrees.get("expiredVotrees").get(0).getType(), is(2));
  }

  @Test
  public void showVotreesTest_profile_성공() {
    Mockito.when(votreeMapper.selectVotreesOnRunningStateByUserId(anyInt())).thenReturn(runningVotrees);
    Mockito.when(votreeMapper.selectVotreesOnExpiredStateByUserId(anyInt())).thenReturn(expiredVotrees);
    Mockito.when(userMapper.selectUserById(runningVotrees.get(0).getProposerId())).thenReturn(runningUser);
    Mockito.when(userMapper.selectUserById(expiredVotrees.get(0).getProposerId())).thenReturn(expiredUser);

    Map<String, List<Votree>> votrees = votreeServiceImpl.showVotrees(0, 0, 0, 12, "profileCall", 1);

    assertThat(votrees.get("runningVotrees").get(0).getId(), is("1"));
    assertThat(votrees.get("runningVotrees").get(0).getTitle(), is("저녁"));
    assertThat(votrees.get("runningVotrees").get(0).getHit(), is(1));
    assertThat(votrees.get("runningVotrees").get(0).getType(), is(1));

    assertThat(votrees.get("expiredVotrees").get(0).getId(), is("2"));
    assertThat(votrees.get("expiredVotrees").get(0).getTitle(), is("아침"));
    assertThat(votrees.get("expiredVotrees").get(0).getHit(), is(2));
    assertThat(votrees.get("expiredVotrees").get(0).getType(), is(2));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void showVotreesTest_mainCall_발의자정보실패() {
    Mockito.when(votreeMapper.selectVotreesOnRunningState(anyInt())).thenReturn(runningVotrees);
    Mockito.when(votreeMapper.selectVotreesOnExpiredState(anyInt())).thenReturn(expiredVotrees);
    Mockito.when(userMapper.selectUserById(runningVotrees.get(0).getProposerId())).thenThrow(Exception.class);
    Mockito.when(userMapper.selectUserById(expiredVotrees.get(0).getProposerId())).thenThrow(Exception.class);

    Map<String, List<Votree>> Votrees = votreeServiceImpl.showVotrees(0, 0, 0, 12, "mainCall", 1);
    assertNull(Votrees.get("runningVotrees").get(0).getProposerName());
    assertNull(Votrees.get("expiredVotrees").get(0).getProposerName());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void showVotreesTest_profileCall_발의자정보실패() {
    Mockito.when(votreeMapper.selectVotreesOnRunningStateByUserId(anyInt())).thenReturn(runningVotrees);
    Mockito.when(votreeMapper.selectVotreesOnExpiredStateByUserId(anyInt())).thenReturn(expiredVotrees);
    Mockito.when(userMapper.selectUserById(runningVotrees.get(0).getProposerId())).thenThrow(Exception.class);
    Mockito.when(userMapper.selectUserById(expiredVotrees.get(0).getProposerId())).thenThrow(Exception.class);

    Map<String, List<Votree>> Votrees = votreeServiceImpl.showVotrees(0, 0, 0, 12, "profileCall", 1);
    assertNull(Votrees.get("runningVotrees").get(0).getProposerName());
    assertNull(Votrees.get("expiredVotrees").get(0).getProposerName());
  }

  @Test(expected = IllegalArgumentException.class)
  public void showVotreesTest_mainCall_발의자정보예외처리() {
    Map<String, List<Votree>> Votrees = votreeServiceImpl.showVotrees(0, 0, 0, 12, "UnsupportedValue", 1);
    assertNull(Votrees);
  }

  @Test
  public void showVotreesTest_MainCall_Expired_성공() {
    Mockito.when(votreeMapper.selectVotreesOnRunningState(anyInt())).thenReturn(runningVotrees);
    Mockito.when(votreeMapper.selectVotreesOnExpiredState(anyInt())).thenReturn(expiredVotrees);
    Mockito.when(userMapper.selectUserById(2)).thenReturn(expiredUser);

    votreeServiceImpl.showVotrees(0, 0, 0, 12, "mainCall", 2);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void registVotree_성공() {
    
    Votree votree = new Votree();
    Vote vote = new Vote();
    
    List<VoteItem> voteItems = new ArrayList<>();
    List<Vote> votes = new ArrayList<>();
    VoteItem voteItem1 = new VoteItem();
    VoteItem voteItem2 = new VoteItem();
    voteItem1.setCategoryId(1);
    voteItem1.setValue("ASDF1111");
    voteItem2.setCategoryId(2);
    voteItem2.setValue("ASDF2222");
    
    voteItems.add(voteItem1);
    voteItems.add(voteItem2);

    vote.setTopic("TESTEST");
    vote.setDuplicateYN("N");
    vote.setVoteItemList(voteItems);
    
    votree.setTitle("테스트");
    votree.setStartDatetime(new Date ( "Sun,5 Dec 1999 00:07:21" ));
    votree.setDueDatetime(new Date());
    votree.setIsPrivate("N");
    votree.setVotes(votes);
    
    
    Mockito.doNothing().when(toastSearchVotree).indexingToToastSearch(votree);
    Mockito.when(votreeMapper.insertVotree(anyObject())).thenReturn(1);
    RestResponse resultResponse = votreeServiceImpl.createVotree(votree, 1);
//    assertTrue(resultResponse.getHeader().getIsSuccessful());
//    assertThat(resultResponse.getHeader().getResultCode(), is(200));
//    assertThat(resultResponse.getHeader().getResultMessage(), is("투표 생성이 성공하였습니다."));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void registVotree_성공_privateVote() {
    Votree votree = new Votree();
    Vote vote = new Vote();
    
    List<VoteItem> voteItems = new ArrayList<>();
    List<Vote> votes = new ArrayList<>();
    VoteItem voteItem1 = new VoteItem();
    VoteItem voteItem2 = new VoteItem();
    voteItem1.setCategoryId(1);
    voteItem1.setValue("ASDF1111");
    voteItem2.setCategoryId(2);
    voteItem2.setValue("ASDF2222");
    
    voteItems.add(voteItem1);
    voteItems.add(voteItem2);

    vote.setTopic("TESTEST");
    vote.setDuplicateYN("N");
    vote.setVoteItemList(voteItems);
    
    votree.setTitle("테스트");
    votree.setStartDatetime(new Date ( "Sun,5 Dec 1999 00:07:21" ));
    votree.setDueDatetime(new Date());
    votree.setIsPrivate("Y");
    votree.setVotes(votes);

    Mockito.doNothing().when(toastSearchVotree).indexingToToastSearch(votree);
    when(votreeMapper.insertVotree(anyObject())).thenReturn(1);
    RestResponse resultResponse = votreeServiceImpl.createVotree(votree, 1);
    RestResponseHeader restResponseHeader = resultResponse.getHeader();
//    assertTrue(restResponseHeader.getIsSuccessful());
//    assertThat(restResponseHeader.getResultCode(), is(200));
//    assertThat(restResponseHeader.getResultMessage(), is("투표 생성이 성공하였습니다."));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void registVotree_성공_이미지() throws Exception {
    Votree votree = new Votree();
    Vote vote = new Vote();
    
    List<VoteItem> voteItems = new ArrayList<>();
    List<Vote> votes = new ArrayList<>();
    VoteItem voteItem1 = new VoteItem();
    VoteItem voteItem2 = new VoteItem();
    voteItem1.setCategoryId(1);
    voteItem1.setValue("ASDF1111");
    voteItem2.setCategoryId(2);
    voteItem2.setValue("image:,456");
    
    voteItems.add(voteItem1);
    voteItems.add(voteItem2);

    vote.setTopic("TESTEST");
    vote.setDuplicateYN("N");
    vote.setVoteItemList(voteItems);
    
    votree.setTitle("테스트");
    votree.setStartDatetime(new Date ( "Sun,5 Dec 1999 00:07:21" ));
    votree.setDueDatetime(new Date());
    votree.setIsPrivate("N");
    votree.setVotes(votes);
    
    
    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("http://www.http.com");
    
    mockStatic(UriComponentsBuilder.class);
    mockStatic(VaildAccessTokenUtil.class);
    when(votreeMapper.insertVotree(anyObject())).thenReturn(1);
    when(toastCloud.getTenantName()).thenReturn("ASDF");
    when(toastCloud.getUserName()).thenReturn("QWER");
    when(toastCloud.getPlainPassword()).thenReturn("PWPW");
    PowerMockito.when(VaildAccessTokenUtil.getCurrentTimeStamp()).thenReturn(new Timestamp(213));
    PowerMockito.when(UriComponentsBuilder.fromHttpUrl(anyString())).thenReturn(uriBuilder);

    Mockito.doNothing().when(toastSearchVotree).indexingToToastSearch(votree);
    VotreeServiceImpl spy = PowerMockito.spy(votreeServiceImpl);

    RestResponse resultResponse = spy.createVotree(votree, 1);
//    assertTrue(resultResponse.getHeader().getIsSuccessful());
//    assertThat(resultResponse.getHeader().getResultCode(), is(200));
//    assertThat(resultResponse.getHeader().getResultMessage(), is("투표 생성이 성공하였습니다."));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void registVotree_성공_동영상() {
    Votree votree = new Votree();
    Vote vote = new Vote();
    
    List<VoteItem> voteItems = new ArrayList<>();
    List<Vote> votes = new ArrayList<>();
    VoteItem voteItem1 = new VoteItem();
    VoteItem voteItem2 = new VoteItem();
    voteItem1.setCategoryId(1);
    voteItem1.setValue("ASDF1111");
    voteItem2.setCategoryId(3);
    voteItem2.setValue("video 111");
    
    voteItems.add(voteItem1);
    voteItems.add(voteItem2);

    vote.setTopic("TESTEST");
    vote.setDuplicateYN("N");
    vote.setVoteItemList(voteItems);
    
    votree.setTitle("테스트");
    votree.setStartDatetime(new Date ( "Sun,5 Dec 1999 00:07:21" ));
    votree.setDueDatetime(new Date());
    votree.setIsPrivate("N");
    votree.setVotes(votes);
    

    Mockito.doNothing().when(toastSearchVotree).indexingToToastSearch(votree);
    when(votreeMapper.insertVotree(anyObject())).thenReturn(1);
    RestResponse resultResponse = votreeServiceImpl.createVotree(votree, 1);
    RestResponseHeader restResponseHeader = resultResponse.getHeader();
//    assertTrue(restResponseHeader.getIsSuccessful());
//    assertThat(restResponseHeader.getResultCode(), is(200));
//    assertThat(restResponseHeader.getResultMessage(), is("투표 생성이 성공하였습니다."));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void registVotree_votree_Null() {
    Votree votree = new Votree();
    Mockito.when(votreeMapper.insertVotree(anyObject())).thenReturn(1);
    
    Mockito.doNothing().when(toastSearchVotree).indexingToToastSearch(votree);
    RestResponse resultResponse = votreeServiceImpl.createVotree(votree, 1);
    RestResponseHeader restResponseHeader = resultResponse.getHeader();
    assertFalse(restResponseHeader.getIsSuccessful());
    assertThat(restResponseHeader.getResultCode(), is(400));
    assertThat(restResponseHeader.getResultMessage(), is("투표 생성이 문제가 발생했습니다. - null"));
  }


  @Test
  public void searchVotrees_Test() throws Exception {
//    List<Votree> dummyVotrees = new ArrayList<>(); 
//    dummyVotrees.addAll(Arrays.asList(
//        new Votree().setId(UUID.randomUUID().toString()).setProposerId(1).setStartDatetime(new Date()),
//        new Votree().setId(UUID.randomUUID().toString()).setProposerId(1),
//        new Votree().setId(UUID.randomUUID().toString()).setProposerId(1)
//        ));
//
//    Mockito.when(votreeMapper.selectVotreesByUserId(anyObject())).thenReturn(dummyVotrees);
//    Mockito.when(userMapper.selectUserById(anyInt())).thenReturn(runningUser);
//    PowerMockito.mockStatic(Collections.class);
//    
//    votrees = votreeServiceImpl.searchVotrees(votreeName, offset);
//
//    assertThat(votrees.size(), is(6));
//    votrees.forEach(votree -> {
//      assertThat(votree.getProposerName() ,is(runningUser.getName()));
//    });
  }

  @Test
  public void findVoteBoxesByVotreeId_Test() {
    List<VoteBox> dummyVoteBoxes = new ArrayList<>();
    dummyVoteBoxes.addAll(Arrays.asList(
        new VoteBox().setUserId(1).setVoteItemId(111).setVoteId(1000),
        new VoteBox().setUserId(1).setVoteItemId(114).setVoteId(1000),
        new VoteBox().setUserId(2).setVoteItemId(111).setVoteId(1000),
        new VoteBox().setUserId(2).setVoteItemId(112).setVoteId(1000),
        new VoteBox().setUserId(3).setVoteItemId(112).setVoteId(1000),
        new VoteBox().setUserId(3).setVoteItemId(113).setVoteId(1000),
        new VoteBox().setUserId(4).setVoteItemId(111).setVoteId(1000),
        new VoteBox().setUserId(4).setVoteItemId(114).setVoteId(1000) //user1 과 user4가 동일한 투표를 한 더미자료
        ));
    Mockito.when(voteResultMapper.selectVoteBoxesByVotreeId(anyObject())).thenReturn(dummyVoteBoxes);
    
    List<Integer> users = votreeServiceImpl.findVoteBoxesByVotreeId("1234", 1);
    
    assertThat(users.size(), is(1));// 같은 투표를 한 유저는 1명
    assertThat(users.get(0), is(4));// 같은 투표를 한 유저의 아이디는 4
  }
  
  
}

